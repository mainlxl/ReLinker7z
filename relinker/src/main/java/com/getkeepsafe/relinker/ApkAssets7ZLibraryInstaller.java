package com.getkeepsafe.relinker;

import android.content.Context;
import android.text.TextUtils;

import com.hzy.lib7z.IExtractCallback;
import com.hzy.lib7z.Z7Extractor;

import java.io.File;

/**
 * @Synopsis 用于扩展7z解压assets中so
 * @Author Mainli
 * @Date 2019/12/9/009
 */
public class ApkAssets7ZLibraryInstaller extends ApkLibraryInstaller {
    @Override
    public void installLibrary(Context context, String[] abis, String mappedLibraryName, File destination, ReLinkerInstance instance) {
        //解压回调在当前线程
        if (Z7Extractor.extractAsset(context.getAssets(), convertExtensionNameTo7z(abis, mappedLibraryName), destination.getParent(), new SingleFileRenameExtractCallback(destination)) != 0) {
            super.installLibrary(context, abis, mappedLibraryName, destination, instance);
        }
    }

    /**
     * 转换为assets下lib目录下.7z文件名
     *
     * @param abis
     * @param libraryName
     * @return
     */
    private String convertExtensionNameTo7z(String[] abis, String libraryName) {
        if (libraryName.length() > 3 && !libraryName.endsWith(".7z")) {
            return String.format("lib/%s/%s.7z", abis[0], libraryName.substring(0, libraryName.length() - 3));
        }
        return libraryName;
    }

    /**
     * 用于解压完成后添加版本号信息
     */
    private static class SingleFileRenameExtractCallback implements IExtractCallback {
        private File destination;
        private String tmpFileName;

        public SingleFileRenameExtractCallback(File destination) {
            this.destination = destination;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onGetFileNum(int fileNum) {

        }

        @Override
        public void onProgress(String name, long size) {
            tmpFileName = name;
        }

        @Override
        public void onError(int errorCode, String message) {

        }

        @Override
        public void onSucceed() {
            String name = destination.getName();
            if (!TextUtils.equals(name, tmpFileName)) {
                File file = new File(destination.getParentFile(), tmpFileName);
                if (destination.exists()) {
                    destination.delete();
                }
                file.renameTo(destination);
            }
        }
    }

}
