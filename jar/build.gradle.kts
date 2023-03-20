import com.oneliang.ktx.gradle.applyCheckKotlinCode

applyCheckKotlinCode()
dependencies {
    implementation(project(":base"))
    implementation(project(":common"))
    implementation(project(":file"))
}