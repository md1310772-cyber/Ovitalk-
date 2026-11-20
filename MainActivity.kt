package com.ovitalk.app

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private lateinit var root: LinearLayout
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FirebaseApp.initializeApp(this)
        } catch (_: Exception) {}
        showHome()
    }

    private fun setupRoot(title: String) {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 36, 28, 24)
            setBackgroundColor(0xFFF7F9FC.toInt())
        }
        val heading = TextView(this).apply {
            text = title
            textSize = 28f
            setTextColor(0xFF1746A2.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }
        root.addView(heading)
        setContentView(root)
    }

    private fun addInput(hint: String): EditText {
        val e = EditText(this).apply {
            this.hint = hint
            textSize = 16f
            setSingleLine(true)
        }
        root.addView(e)
        return e
    }

    private fun addButton(label: String, action: () -> Unit) {
        val b = Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { action() }
        }
        root.addView(b)
    }

    private fun showHome() {
        if (auth.currentUser != null) { showChat(); return }
        setupRoot("OviTalk")
        val note = TextView(this).apply {
            text = "ফোন নম্বর দিয়ে লগইন করুন"
            textSize = 17f
            setPadding(0, 0, 0, 16)
        }
        root.addView(note)
        val phone = addInput("ফোন নম্বর: +8801XXXXXXXXX")
        addButton("OTP পাঠান") {
            val number = phone.text.toString().trim()
            if (!number.startsWith("+") || number.length < 10) {
                toast("দেশের কোডসহ নম্বর লিখুন, যেমন +8801XXXXXXXXX")
            } else sendOtp(number)
        }
        val code = addInput("৬ সংখ্যার OTP")
        addButton("লগইন করুন") {
            val id = verificationId
            val otp = code.text.toString().trim()
            if (id.isNullOrBlank() || otp.length != 6) {
                toast("আগে OTP নিন, তারপর ৬ সংখ্যার কোড লিখুন")
            } else {
                val credential = PhoneAuthProvider.getCredential(id, otp)
                auth.signInWithCredential(credential).addOnSuccessListener {
                    saveUser()
                    showChat()
                }.addOnFailureListener { toast(it.localizedMessage ?: "লগইন হয়নি") }
            }
        }
    }

    private fun sendOtp(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    auth.signInWithCredential(credential).addOnSuccessListener {
                        saveUser(); showChat()
                    }.addOnFailureListener { toast(it.localizedMessage ?: "লগইন হয়নি") }
                }
                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    toast(e.localizedMessage ?: "OTP পাঠানো যায়নি")
                }
                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                    toast("OTP পাঠানো হয়েছে")
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun saveUser() {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).set(
            mapOf("phone" to user.phoneNumber, "createdAt" to System.currentTimeMillis())
        )
    }

    private fun showChat() {
        setupRoot("OviTalk")
        val user = auth.currentUser
        val status = TextView(this).apply {
            text = "লগইন হয়েছে: ${user?.phoneNumber ?: ""}"
            textSize = 14f
            setPadding(0, 0, 0, 16)
        }
        root.addView(status)
        val info = TextView(this).apply {
            text = "এটি OviTalk-এর প্রথম মেসেজিং সংস্করণ। নিরাপদ ব্যক্তিগত চ্যাট চালুর আগে চ্যাট সদস্য যাচাইসহ Firestore Rules সেট করতে হবে।"
            textSize = 16f
            setPadding(0, 8, 0, 20)
        }
        root.addView(info)
        val peer = addInput("বন্ধুর UID (পরবর্তী ধাপে যুক্ত হবে)")
        val message = addInput("মেসেজ লিখুন")
        addButton("মেসেজ পাঠান") {
            toast("চ্যাট সদস্য যাচাই ও নিরাপদ chat ID সেট না হওয়া পর্যন্ত মেসেজ পাঠানো বন্ধ রাখা হয়েছে।")
        }
        addButton("লগআউট") {
            auth.signOut()
            showHome()
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
