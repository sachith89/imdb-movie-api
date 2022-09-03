package com.sachith.imdb_api.config;

import com.sachith.imdb_api.entity.NameBasics;
import org.springframework.batch.item.ItemProcessor;

public class NameBasicsItemProcessor implements ItemProcessor<NameBasics,NameBasics> {

    @Override
    public NameBasics process(NameBasics nameBasics) throws Exception {
        return nameBasics;
    }
}