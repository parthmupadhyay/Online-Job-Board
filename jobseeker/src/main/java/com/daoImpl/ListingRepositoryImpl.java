package com.daoImpl;

import com.dao.ListingRepository;
import com.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by karan on 5/17/2017.
 */
@Service
public class ListingRepositoryImpl {
    private final static int PAGESIZE = 10;

    @Autowired
    ListingRepository listingRepository;


    public List<Position> getPage(int pageNumber) {
        PageRequest request = new PageRequest(pageNumber - 1, PAGESIZE);
        return listingRepository.findAll(request).getContent();
    }
}
