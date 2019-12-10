**编译时移动并压缩Lib目录下指定so到assets目录对应Lib下**

结合[ReLinker](https://github.com/KeepSafe/ReLinker)与[AndroidUn7z](https://github.com/hzy3774/AndroidUn7zip)实现

通过脚本[so7Z.gradle](sample/so7Z.gradle)实现移动压缩so到assets下,压缩格式为7z

扩展ReLinker的[ApkLibraryInstaller](relinker/src/main/java/com/getkeepsafe/relinker/ApkLibraryInstaller.java) -> [ApkAssets7ZLibraryInstaller.java](relinker/src/main/java/com/getkeepsafe/relinker/ApkAssets7ZLibraryInstaller.java)实现解压与加载





