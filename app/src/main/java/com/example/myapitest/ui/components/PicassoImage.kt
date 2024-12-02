import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapitest.utils.CircleTransform
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


@Composable
fun PicassoImage(
    url: String,
    contentDescription: String? = null,
    modifier: Modifier

) {
    AndroidView(
        factory = { context ->
            ImageView(context).apply {
                this.contentDescription = contentDescription
                layoutParams = ViewGroup.LayoutParams(150, 150)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Picasso.get()
                .load(url)
                .transform(CircleTransform())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView, object : Callback {
                    override fun onSuccess() {}

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                    }
                })
        },
        modifier = modifier
    )
}