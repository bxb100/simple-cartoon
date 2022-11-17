package com.xiaobo.cartoon.volume;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.xiaobo.cartoon.comic.Comic;
import com.xiaobo.cartoon.comic.ComicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import static com.xiaobo.cartoon.volume.Volume.ORDER_RULE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolumeService {

	private final VolumeRepository volumeRepository;
	private final ComicRepository comicRepository;

	private static String encodeFileName(String fileName) {
		return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
	}

	public List<Volume> findByComicId(String comicId) {
		if (!ObjectId.isValid(comicId)) {
			return List.of();
		}

		return volumeRepository.findAllByComicId(new ObjectId(comicId))
				.stream()
				.sorted(ORDER_RULE)
				.toList();
	}

	public Volume getById(String id) {
		return volumeRepository.findById(id).orElseThrow();
	}

	public void deleteById(String id) {

		volumeRepository.findById(id).ifPresent(volume -> {
			// reset previous volume's nextId
			Optional.ofNullable(volume.getPreviousId())
					.flatMap(volumeRepository::findById)
					.ifPresent(previous -> {
						previous.setNextId(volume.getNextId());
						volumeRepository.save(previous);
					});

			// reset next volume's previousId
			Optional.ofNullable(volume.getNextId())
					.flatMap(volumeRepository::findById)
					.ifPresent(next -> {
						next.setPreviousId(volume.getPreviousId());
						volumeRepository.save(next);
					});

			volumeRepository.delete(volume);

			// update comic's volume count
			comicRepository.findById(volume.getComicId().toString()).ifPresent(comic -> {
				comic.setVolNum(comic.getVolNum() - 1);
				comicRepository.save(comic);
			});

		});
	}

	public void markRead(String id, Integer currentPage) {
		Optional<Volume> volume = volumeRepository.findById(id);
		volume.ifPresent(v -> {
			v.setCurrentPage(currentPage);
			v.setRead(true);
			volumeRepository.save(v);
		});
	}

	public List<Volume> associate(Comic comic, List<Volume> volumes) {

		List<Volume> saved = volumeRepository.saveAll(volumes);

		for (int i = 0; i < saved.size(); i++) {
			Volume volume = saved.get(i);
			volume.setComicId(new ObjectId(comic.getId()));
			volume.setPreviousId(null);
			volume.setNextId(null);
			if (i > 0) {
				volume.setPreviousId(saved.get(i - 1).getId());
			}
			if (i < saved.size() - 1) {
				volume.setNextId(saved.get(i + 1).getId());
			}
		}

		return volumeRepository.saveAll(saved);
	}

	public void cleanUp(Comic comic, List<Volume> volumes) {
		if (volumes.isEmpty()) {
			return;
		}

		List<Volume> allByComicId = volumeRepository.findAllByComicId(new ObjectId(comic.getId()));

		List<Volume> orphan = allByComicId.stream().filter(p -> volumes.stream().noneMatch(v -> v.getId().equals(p.getId())))
				.toList();

		volumeRepository.deleteAll(orphan);
	}

	public ResponseEntity<StreamingResponseBody> read(String id) throws IOException {

		Volume volume = volumeRepository.findById(id).orElseThrow();

		Path path = Paths.get(volume.getPath());
		long fileSize = Files.size(path);
		String filename = volume.getFileName();
		String fileType = URLConnection.guessContentTypeFromName(path.getFileName().toString());

		log.info("{} {} {} {}", filename, fileType, fileSize, path);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + encodeFileName(filename))
				.contentLength(fileSize)
				.contentType(MediaType.parseMediaType(fileType))
				.cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.body(outputStream -> {
					try (InputStream is = Files.newInputStream(path)) {
						is.transferTo(outputStream);
					}
				});
	}
}
