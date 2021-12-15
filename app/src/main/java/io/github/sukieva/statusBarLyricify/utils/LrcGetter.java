package io.github.sukieva.statusBarLyricify.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.zhaiyifan.lyric.LyricUtils;
import cn.zhaiyifan.lyric.model.Lyric;
import io.github.sukieva.statusBarLyricify.data.Media;
import io.github.sukieva.statusBarLyricify.provider.ILrcProvider;
import io.github.sukieva.statusBarLyricify.provider.KugouProvider;
import io.github.sukieva.statusBarLyricify.provider.NeteaseProvider;
import io.github.sukieva.statusBarLyricify.provider.QQMusicProvider;
import io.github.sukieva.statusBarLyricify.provider.utils.LyricSearchUtil;

public class LrcGetter {

    private static final ILrcProvider[] providers = {
            new KugouProvider(),
            new QQMusicProvider(),
            new NeteaseProvider()
    };

    private static MessageDigest messageDigest;
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static Lyric getLyric(Context context, Media mediaMetadata) {
        if (messageDigest == null) {
            try {
                messageDigest = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        File cachePath = context.getCacheDir();
        String meta = mediaMetadata.getTitle() + "," + mediaMetadata.getArtist() + "," +
                mediaMetadata.getAlbum() + ", " + mediaMetadata.getDuration();
        File requireLrcPath = new File(cachePath, printHexBinary(messageDigest.digest(meta.getBytes())) + ".lrc");
        if (requireLrcPath.exists()) {
            return LyricUtils.parseLyric(requireLrcPath, "UTF-8");
        }
        ILrcProvider.LyricResult currentResult = null;
        for (ILrcProvider provider : providers) {
            try {
                ILrcProvider.LyricResult lyricResult = provider.getLyric(mediaMetadata);
                if (lyricResult != null && LyricSearchUtil.isLyricContent(lyricResult.mLyric) && (currentResult == null || currentResult.mDistance > lyricResult.mDistance)) {
                    currentResult = lyricResult;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (currentResult != null && LyricSearchUtil.isLyricContent(currentResult.mLyric)) {
            try {
                FileOutputStream lrcOut = new FileOutputStream(requireLrcPath);
                lrcOut.write(currentResult.mLyric.getBytes());
                lrcOut.close();
                return LyricUtils.parseLyric(requireLrcPath, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[b & 0xF]);
        }
        return r.toString();
    }
}
