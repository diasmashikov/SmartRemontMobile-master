package kz.cheesenology.smartremontmobile.network

import kz.cheesenology.smartremontmobile.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject


class RetrofitInterceptor private constructor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()

        val newUrl = original.url.newBuilder()
            .scheme("https")
            .host(BuildConfig.BASE_HOST)
            .build()

        original = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(original)
    }

    companion object {
        var sInterceptor: RetrofitInterceptor? = null

        fun get(): RetrofitInterceptor {
            if (sInterceptor == null) {
                sInterceptor = RetrofitInterceptor()
            }
            return sInterceptor as RetrofitInterceptor
        }
    }
}
