package com.xiaobo.cartoon.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.xiaobo.cartoon.volume.Volume;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CompressionUtil {

	public void mapMetaData(Volume volume) throws ImageException, IOException {
		try (FileSystem fs = FileSystems.newFileSystem(Paths.get(volume.getPath()))) {
			List<Path> entries = ZipReaderUtil.getEntries(fs);

			volume.setPageCount(
					entries.stream()
							.filter(Files::isRegularFile)
							.filter(ZipReaderUtil::isImage)
							.count()
			);

			if (volume.getPageCount() <= 0) {
				throw new ImageException();
			}

			Path path = ZipReaderUtil.getImages(fs).get(0);
			try (InputStream is = Files.newInputStream(path)) {
				volume.setCover(ThumbnailUtil.get(is).toByteArray());
			}
		}
	}
}
