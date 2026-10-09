@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
module shared.jvm {
    requires transitive kotlin.stdlib;
    requires transitive kotlinx.coroutines.core;
    requires transitive kotlinx.serialization.core;
    requires transitive kotlinx.serialization.json;
    requires transitive io.github.oshai.kotlinlogging;
    requires transitive io.ktor.client.core;
    requires transitive io.ktor.http;
    requires transitive io.ktor.utils;
    requires transitive io.ktor.resources;
    requires transitive io.ktor.client.resources;
    requires io.ktor.client.java;
    requires io.ktor.client.logging;
    requires io.ktor.client.content.negotiation;
    requires io.ktor.serialization.kotlinx.json;
    requires nl.altindag.ssl;
    requires certkit;
    requires java.net.http;
    requires jdk.incubator.vector;
    requires static com.google.auto.service;

    // exports dev.suresh;
    // exports dev.suresh.ffm;
    // exports dev.suresh.http;
    // exports dev.suresh.spi;
}
