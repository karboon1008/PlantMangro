package com.example.recycleviewwithclicklistener

import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList

class SignatureHelper(context: Context?) :
    ContextWrapper(context) {
    // For each signature create a compatible hash
    /**
     * Get all the app signatures for the current package
     */
    val appSignature: ArrayList<String>
        get() {
            val appCodes = ArrayList<String>()
            try {
                // Get all package signatures for the current package
                val myPackageName = packageName
                val myPackageManager = packageManager
                val signatures = myPackageManager.getPackageInfo(myPackageName,PackageManager.GET_SIGNATURES).signatures
                // For each signature create a compatible hash
                for (signature in signatures) {
                    val hash = hash(myPackageName, signature.toCharsString())
                    if (hash != null) {
                        appCodes.add(String.format("%s", hash))
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.d(TAG,"Package not found",e)
            }
            return appCodes
        }

    companion object {
        private const val HASH_TYPE = "SHA-256"
        const val HASHED_BYTES = 9
        const val BASE64_CHAR = 11
        private fun hash(pkgName: String, signature: String): String? {
            val appInfo = "$pkgName $signature"
            try {
                val messageDigest =  MessageDigest.getInstance(HASH_TYPE)
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
                var myHashSignature = messageDigest.digest()
                // truncated into HASHED_BYTES
                myHashSignature = Arrays.copyOfRange(myHashSignature,0,HASHED_BYTES)
                // encode into Base64
                var base64Hash = Base64.encodeToString(myHashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
                base64Hash = base64Hash.substring(0, BASE64_CHAR)
                Log.d(TAG, String.format("pkg: %s -- hash: %s", pkgName, base64Hash))
                return base64Hash
            } catch (error: NoSuchAlgorithmException) {
                Log.e(TAG, "Algorithm not Found", error)
            }
            return null
        }
    }
}