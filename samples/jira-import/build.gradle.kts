description = "Jira Import Sample"

tasks.existing(Wrapper::class) {
    gradleVersion = "4.10.2"
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    java
}

tasks.withType(type = JavaCompile::class) {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral();
    mavenLocal()
}

dependencies {
    compile("io.qameta.allure:allure-junit4:2.8.1")

    compile("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}
