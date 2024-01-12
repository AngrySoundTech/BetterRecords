import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.0.1"
}

val copyDokka = tasks.register<Copy>("copyDokka") {
    dependsOn(":dokkaHtml")
    from("../build/dokka/html")
    into("src/public/dokka")
}

val devTask = tasks.register<NpmTask>("dev") {
    dependsOn(":docs:npmInstall")
    dependsOn(":docs:copyDokka")
    args.addAll("run", "dev")
}

val buildTask = tasks.register<NpmTask>("build") {
    dependsOn(":docs:npmInstall")
    dependsOn(":docs:copyDokka")
    args.addAll("run", "build")
}