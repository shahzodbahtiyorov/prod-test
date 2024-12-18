package org.finance.ui.paynet.src;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import java.io.IOException;
import java.io.InputStream;

public class SvgDecoder implements ResourceDecoder<InputStream, PictureDrawable> {
    @Override
    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
        return true;
    }

    @Override
    public Resource<PictureDrawable> decode(@NonNull InputStream source, int width, int height, @NonNull Options options) throws IOException {
        try {
            SVG svg = SVG.getFromInputStream(source);

            if (width <= 0 || height <= 0) {
                width = (int) svg.getDocumentWidth();
                height = (int) svg.getDocumentHeight();
            }

            svg.setDocumentWidth(width);
            svg.setDocumentHeight(height);

            Picture picture = svg.renderToPicture(width, height);
            PictureDrawable pictureDrawable = new PictureDrawable(picture);
            return new SimpleResource<>(pictureDrawable);
        } catch (Exception e) {
            throw new IOException("SVG could not be loaded", e);
        }
    }
}
