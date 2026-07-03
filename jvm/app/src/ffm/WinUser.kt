package ffm

import dev.suresh.ffm.C_INT
import dev.suresh.ffm.C_POINTER
import dev.suresh.ffm.WCHAR
import dev.suresh.ffm.downcallHandle
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment

/**
 * Windows user identity information retrieved via FFM calls to kernel32/advapi32.
 *
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/securitybaseapi/">Security
 *   API</a>
 */
object WinUser {

  /** Current user's login name, or `null` if unavailable. */
  val username: String?

  /** Current user's SID (e.g., "S-1-5-21-..."). */
  val userSid: String?

  /** Current user's primary group SID. */
  val primaryGroupSid: String?

  /** Current user's group SIDs. */
  val groupSids: List<String>

  private const val TOKEN_QUERY = 0x0008
  private const val TOKEN_USER = 1
  private const val TOKEN_GROUPS = 2
  private const val TOKEN_PRIMARY_GROUP = 5
  private const val SID_ATTR_SIZE = 16L
  private const val TOKEN_GROUPS_ARRAY_OFFSET = 8L

  private val mhGetCurrentProcess by lazy {
    downcallHandle("GetCurrentProcess", FunctionDescriptor.of(C_POINTER))
  }

  private val mhOpenProcessToken by lazy {
    downcallHandle("OpenProcessToken", FunctionDescriptor.of(C_INT, C_POINTER, C_INT, C_POINTER))
  }

  private val mhGetTokenInformation by lazy {
    downcallHandle(
        "GetTokenInformation",
        FunctionDescriptor.of(C_INT, C_POINTER, C_INT, C_POINTER, C_INT, C_POINTER),
    )
  }

  private val mhGetUserNameW by lazy {
    downcallHandle("GetUserNameW", FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER))
  }

  private val mhConvertSidToStringSidW by lazy {
    downcallHandle("ConvertSidToStringSidW", FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER))
  }

  private val mhCloseHandle by lazy {
    downcallHandle("CloseHandle", FunctionDescriptor.of(C_INT, C_POINTER))
  }

  private val mhLocalFree by lazy {
    downcallHandle("LocalFree", FunctionDescriptor.of(C_POINTER, C_POINTER))
  }

  private val mhGetLastError by lazy {
    downcallHandle("GetLastError", FunctionDescriptor.of(C_INT))
  }

  init {
    Arena.ofConfined().use { arena ->
      username = fetchUsername(arena)

      val tokenPtr = arena.allocate(C_POINTER)
      val process = mhGetCurrentProcess.invokeExact() as MemorySegment

      if ((mhOpenProcessToken.invokeExact(process, TOKEN_QUERY, tokenPtr) as Int) == 0) {
        val err = mhGetLastError.invokeExact() as Int
        error("OpenProcessToken failed with error $err")
      }

      val token = tokenPtr.get(C_POINTER, 0)
      try {
        userSid = fetchSid(arena, token, TOKEN_USER)
        primaryGroupSid = fetchSid(arena, token, TOKEN_PRIMARY_GROUP)
        groupSids = fetchGroupSids(arena, token)
      } finally {
        mhCloseHandle.invokeExact(token)
      }
    }
  }

  private fun fetchUsername(arena: Arena): String? {
    val sizePtr = arena.allocate(C_INT).also { it.set(C_INT, 0, 256) }
    val buffer = arena.allocate(WCHAR, 256)
    if ((mhGetUserNameW.invokeExact(buffer, sizePtr) as Int) == 0) return null
    val len = sizePtr.get(C_INT, 0) - 1
    return if (len > 0) readWideString(buffer, len) else null
  }

  private fun fetchSid(arena: Arena, token: MemorySegment, infoClass: Int): String? {
    val info = getTokenInfo(arena, token, infoClass) ?: return null
    return sidToString(arena, info.get(C_POINTER, 0))
  }

  private fun fetchGroupSids(arena: Arena, token: MemorySegment): List<String> {
    val info = getTokenInfo(arena, token, TOKEN_GROUPS) ?: return []
    val count = info.get(C_INT, 0)
    return (0 until count).mapNotNull { i ->
      sidToString(arena, info.get(C_POINTER, TOKEN_GROUPS_ARRAY_OFFSET + i * SID_ATTR_SIZE))
    }
  }

  private fun getTokenInfo(arena: Arena, token: MemorySegment, infoClass: Int): MemorySegment? {
    val sizePtr = arena.allocate(C_INT)
    mhGetTokenInformation.invokeExact(token, infoClass, MemorySegment.NULL, 0, sizePtr)
    val size = sizePtr.get(C_INT, 0)
    if (size <= 0) return null

    val buffer = arena.allocate(size.toLong())
    val ok = mhGetTokenInformation.invokeExact(token, infoClass, buffer, size, sizePtr) as Int
    return if (ok != 0) buffer else null
  }

  private fun sidToString(arena: Arena, sid: MemorySegment): String? {
    if (sid.address() == 0L) return null
    val strPtr = arena.allocate(C_POINTER)
    if ((mhConvertSidToStringSidW.invokeExact(sid, strPtr) as Int) == 0) return null

    val strSeg = strPtr.get(C_POINTER, 0)
    val result = readWideStringNullTerminated(strSeg)
    mhLocalFree.invokeExact(strSeg)
    return result
  }

  private fun readWideString(seg: MemorySegment, len: Int): String =
      CharArray(len) { seg.get(WCHAR, it * 2L) }.concatToString()

  private fun readWideStringNullTerminated(seg: MemorySegment): String = buildString {
    var pos = 0L
    while (true) {
      val c = seg.get(WCHAR, pos)
      if (c == '\u0000') break
      append(c)
      pos += 2
    }
  }

  override fun toString() =
      "WinUser(username=$username, userSid=$userSid, primaryGroupSid=$primaryGroupSid, groupSids=$groupSids)"
}
