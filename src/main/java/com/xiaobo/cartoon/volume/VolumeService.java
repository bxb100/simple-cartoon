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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolumeService {

	private final VolumeRepository volumeRepository;


	public List<Volume> findByComicId(String comicId) {

		return volumeRepository.findAllByComicId(new ObjectId(comicId));
	}

	public Volume getById(String id) {
		return volumeRepository.findById(id).orElseThrow();
	}

	public void deleteById(String id) {

		volumeRepository.deleteById(id);
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

		for (int i = 0; i < volumes.size(); i++) {
			Volume volume = volumes.get(i);
			volume.setComicId(new ObjectId(comic.getId()));
			if (i > 0) {
				volume.setPreviousId(volumes.get(i - 1).getId());
			}
			if (i < volumes.size() - 1) {
				volume.setNextId(volumes.get(i + 1).getId());
			}
		}

		return volumeRepository.saveAll(volumes);
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

	private static String encodeFileName(String fileName) {
		return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
	}
}
