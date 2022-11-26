package io.apidocx.base.sdk.rap2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

public class SvgUtils {

    private SvgUtils() {
    }

    public static byte[] convertToPngBytes(byte[] svg) {
        PNGTranscoder t = new PNGTranscoder();
        return transcode(t, svg);
    }

    public static byte[] convertToJpegBytes(byte[] svg) {
        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, .8f);
        return transcode(t, svg);
    }

    public static byte[] transcode(ImageTranscoder transcoder, byte[] svg) {
        ByteArrayOutputStream bytesOs = new ByteArrayOutputStream();
        TranscoderInput svgInput = new TranscoderInput(new ByteArrayInputStream(svg));
        TranscoderOutput pngOutput = new TranscoderOutput(bytesOs);
        try {
            transcoder.transcode(svgInput, pngOutput);
            return bytesOs.toByteArray();
        } catch (TranscoderException e) {
            throw new RuntimeException("svg图片转换失败", e);
        } finally {
            try {
                bytesOs.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
