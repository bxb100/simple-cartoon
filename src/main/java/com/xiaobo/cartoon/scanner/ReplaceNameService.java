package com.xiaobo.cartoon.scanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.xiaobo.cartoon.volume.Volume;
import com.xiaobo.cartoon.volume.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplaceNameService {

	private final VolumeRepository volumeRepository;

	/**
	 * Rule group:
	 * 1. Pattern, replacement, effectIds
	 * 2. IdMapName
	 */
	@Transactional
	public Map<String, String> replaceVolumeName(
			String comicId, ReplaceFileNameReq req, boolean forShow
	) {
		List<Volume> volumes = volumeRepository.findAllByComicId(new ObjectId(comicId));
		Optional<Pattern> pattern = Optional.ofNullable(req.pattern()).map(Pattern::compile);

		Map<String, String> idMapName = Optional.ofNullable(req.idMapName()).orElse(new HashMap<>());

		List<String> effectIds = Optional.ofNullable(req.effectIds()).orElse(volumes.stream().map(Volume::getId).toList());

		volumes.stream()
				.filter(v -> effectIds.contains(v.getId()))
				.forEach(v -> pattern.ifPresent(p -> {
					String newName = p.matcher(v.getFileName()).replaceAll(req.replacement());
					idMapName.putIfAbsent(v.getId(), newName);
				}));

		if (!forShow) {
			List<Volume> needSave = volumes.stream().filter(v -> idMapName.containsKey(v.getId()))
					.peek(v -> v.setFileName(idMapName.get(v.getId())))
					.toList();
			volumeRepository.saveAll(needSave);
		}
		return idMapName;
	}
}
