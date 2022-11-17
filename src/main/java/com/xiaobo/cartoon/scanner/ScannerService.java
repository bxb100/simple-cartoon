package com.xiaobo.cartoon.scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.xiaobo.cartoon.comic.Comic;
import com.xiaobo.cartoon.comic.ComicRepository;
import com.xiaobo.cartoon.util.CompressionUtil;
import com.xiaobo.cartoon.util.ImageException;
import com.xiaobo.cartoon.volume.Volume;
import com.xiaobo.cartoon.volume.VolumeRepository;
import com.xiaobo.cartoon.volume.VolumeService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.xiaobo.cartoon.volume.Volume.ORDER_RULE;

@Service
@RequiredArgsConstructor
public class ScannerService {

	private final VolumeService volumeService;
	private final ComicRepository comicRepository;
	private final VolumeRepository volumeRepository;

	private boolean isCompressionFile(Path path) {
		String s = path.getFileName().toString();
		return Stream.of(".zip", ".cbz", ".rar", ".tar")
				.anyMatch(s::endsWith);
	}

	@Transactional
	public int scan(String comicId) {

		Comic comic = comicRepository.findById(comicId).orElseThrow();

		Path volumesPath = Paths.get(comic.getPath());
		try (Stream<Path> paths = Files.walk(volumesPath)) {

			// 1. Scan current comic define path
			List<Volume> volumes = paths
					// 2. get all satisfied files
					.filter(Files::isRegularFile)
					.filter(this::isCompressionFile)
					.map(path -> this.processVolumeByPath(comic.getId(), path))
					.sorted(ORDER_RULE)
					.toList();

			// 3. persist into volume repo
			comic.setScanned(true);
			comic.setVolNum(volumes.size());
			Comic save = comicRepository.save(comic);
			List<Volume> associate = volumeService.associate(save, volumes);

			// 4. clean orphan
			volumeService.cleanUp(save, associate);

			return associate.size();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Volume processVolumeByPath(String comicId, Path path) {
		Volume volume = volumeRepository.findByPathAndComicId(path.toString(), new ObjectId(comicId))
				.orElse(new Volume(0));

		if (volume.getId() == null) {
			// ensure not to influence existed data
			volume.setPath(path.toAbsolutePath().toString());
			volume.setFileName(path.getFileName().toString());
		}

		if (volume.getCover() == null) {
			try {
				CompressionUtil.mapMetaData(volume);
			} catch (ImageException | IOException e) {
				List<String> errors = Optional.ofNullable(volume.getScannerErrors()).orElse(new ArrayList<>());
				errors.add(e.getMessage());
				volume.setScannerErrors(errors);
			}
		}

		return volume;
	}
}
