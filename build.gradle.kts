plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "com.cat"
version = "0.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies{
    // Baidu API
    implementation("com.baidu.aip:java-sdk:4.0.0")
    // FastJSON
    implementation("com.alibaba:fastjson:2.0.45")
    // OkHttp3
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    // Json
    implementation("org.json:json:20231013")
    // Yaml
    implementation("org.yaml:snakeyaml:2.2")
}
mirai {
    jvmTarget = JavaVersion.VERSION_1_8
}
