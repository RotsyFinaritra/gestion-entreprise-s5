package com.entreprise.service;

import com.entreprise.model.Genre;
import com.entreprise.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    
    @Autowired
    private GenreRepository genreRepository;
    
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }
    
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }
    
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }
    
    public void deleteById(Long id) {
        genreRepository.deleteById(id);
    }
}
