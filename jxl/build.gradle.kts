import com.oneliang.ktx.gradle.applyCheckKotlinCode

applyCheckKotlinCode()
dependencies {
    implementation(project(":base"))
    implementation(project(":common"))
    implementation(project(":file"))
    implementation(Dependencies["jexcelapi-jxl"])
//    implementation project(":libraries:3rd-jxl")
}