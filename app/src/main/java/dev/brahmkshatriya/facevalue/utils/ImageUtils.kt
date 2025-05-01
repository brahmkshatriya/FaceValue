package dev.brahmkshatriya.facevalue.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import coil3.Image
import coil3.asDrawable
import coil3.imageLoader
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.placeholder
import coil3.request.target
import coil3.request.transformations
import coil3.transform.Transformation
import dev.brahmkshatriya.facevalue.models.ImageHolder

object ImageUtils {

    private fun <T> tryWith(print: Boolean = false, block: () -> T): T? {
        return try {
            block()
        } catch (e: Throwable) {
            if (print) e.printStackTrace()
            null
        }
    }

    private suspend fun <T> tryWithSuspend(print: Boolean = true, block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Throwable) {
            if (print) e.printStackTrace()
            null
        }
    }

    private fun View.enqueue(builder: ImageRequest.Builder) =
        context.imageLoader.enqueue(builder.build())

    fun ImageHolder?.loadInto(
        imageView: ImageView, placeholder: Int? = null, errorDrawable: Int? = null
    ) = tryWith {
        val request = createRequest(imageView.context, placeholder, errorDrawable)
        request.target(imageView)
        imageView.enqueue(request)
    }

    fun ImageHolder?.loadInto(
        view: View,
        placeholder: Int? = null,
        errorDrawable: Int? = null,
        onDrawable: (Drawable?) -> Unit
    ) {
        tryWith {
            val request = createRequest(view.context, placeholder, errorDrawable)
            val target = { it: Image? ->
                onDrawable(it?.asDrawable(view.resources))
            }
            request.target(target, target, target)
            view.enqueue(request)
        }
    }

    suspend fun ImageHolder?.loadDrawable(
        context: Context
    ) = tryWithSuspend {
        val request = createRequest(context, null, null)
        context.imageLoader.execute(request.build()).image?.asDrawable(context.resources)
    }

    private fun createRequest(
        imageHolder: ImageHolder,
        builder: ImageRequest.Builder,
    ) = imageHolder.run {
        val key = hashCode().toString()
        when (this) {
            is ImageHolder.UriImageHolder -> builder.data(uri)
            is ImageHolder.UrlRequestImageHolder -> {
                if (request.headers.isNotEmpty())
                    builder.httpHeaders(NetworkHeaders.Builder().apply {
                        request.headers.forEach { (t, u) -> add(t, u) }
                    }.build())
                builder.data(request.url)
            }
        }.diskCacheKey(key)
    }

    private fun ImageHolder?.createRequest(
        context: Context,
        placeholder: Int?,
        errorDrawable: Int?,
        vararg transformations: Transformation
    ): ImageRequest.Builder {
        val builder = ImageRequest.Builder(context)
        var error = errorDrawable
        if (error == null) error = placeholder

        if (this == null) {
            if (error != null) builder.data(error)
            return builder
        }
        createRequest(this, builder)
        placeholder?.let { builder.placeholder(it) }
        error?.let { builder.error(it) }
        val list = transformations.toList()
        if (list.isNotEmpty()) builder.transformations(list)
        return builder
    }
}