package org.finance.ui.paynet.src;

import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

public class SvgSoftwareLayerSetter implements RequestListener<PictureDrawable> {
    public static void applySoftwareLayer(ImageView imageView) {
        imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<PictureDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(PictureDrawable resource, Object model, Target<PictureDrawable> target, DataSource dataSource, boolean isFirstResource) {
        if (target instanceof ImageViewTarget) {
            ImageView imageView = ((ImageViewTarget<PictureDrawable>) target).getView();
            applySoftwareLayer(imageView);
        }
        return false;
    }
}
