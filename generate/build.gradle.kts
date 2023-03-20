import com.oneliang.ktx.gradle.applyCheckKotlinCode

applyCheckKotlinCode()
dependencies {
    implementation(project(":base"))
    implementation(project(":json"))
    implementation(project(":file"))
    implementation(project(":common"))
}