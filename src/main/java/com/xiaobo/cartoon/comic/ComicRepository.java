package com.xiaobo.cartoon.comic;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComicRepository extends MongoRepository<Comic, String> {

	List<Comic> findAllByOrderByTitleAsc();
}
