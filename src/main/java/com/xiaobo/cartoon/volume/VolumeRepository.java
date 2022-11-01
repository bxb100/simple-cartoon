package com.xiaobo.cartoon.volume;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VolumeRepository extends MongoRepository<Volume, String> {

	List<Volume> findAllByComicId(ObjectId comicId);

	Optional<Volume> findByPathAndComicId(String path, ObjectId comicId);

	void deleteAllByComicId(ObjectId comicId);
}
