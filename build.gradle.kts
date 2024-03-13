plugins {
  id("java")
  id("maven-publish")
}

group = "com.symentis"

version = if (project.hasProperty("github_release_version")) {
  // releases are triggered manually by creating a GitHub release
  // the release triggers `release.yaml` GH action which sets the `github_release_version` property
  project.property("github_release_version") as String
} else {
  "0.7.5-SNAPSHOT"
}

println("Version: $version")

// This is a sanity check, don't remove
if (!project.hasProperty("github_release_version") && !version.toString().endsWith("-SNAPSHOT")) {
  error("github_release_version is not set and version does not end with -SNAPSHOT")
}

description = "A simple JIRA REST client"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
  withSourcesJar()
  withJavadocJar()
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.apache.httpcomponents:httpclient:4.5.10")
  implementation("org.apache.httpcomponents:httpmime:4.5.10")
  implementation("org.apache.commons:commons-lang3:3.8")
  implementation("net.sf.json-lib:json-lib:2.4:jdk15")
  implementation("joda-time:joda-time:2.3")
  implementation("org.scribe:scribe:1.3.7")

  testImplementation("junit:junit:4.12")
  testImplementation("org.powermock:powermock-module-junit4:1.6.3")
  testImplementation("org.powermock:powermock-api-mockito:1.6.3")
  testImplementation("org.codehaus.groovy:groovy-all:2.4.6")
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
    }
  }

  repositories {
    maven {
      name = "GithubPackages"
      url = uri("https://maven.pkg.github.com/symentis/jira-client")
      credentials(PasswordCredentials::class) {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
}
