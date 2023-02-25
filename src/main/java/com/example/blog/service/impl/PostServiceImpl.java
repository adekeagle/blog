package com.example.blog.service.impl;

import com.example.blog.dto.PostDto;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.model.Category;
import com.example.blog.model.Post;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.response.PostResponse;
import com.example.blog.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final ModelMapper mapper;

    private final CategoryRepository categoryRepository;

    @Override
    public PostDto createPost(PostDto postDto) {
        log.info("creating new post");
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        // konwersja dto do modelu
        Post post = mapToEntity(postDto);
        post.setCategory(category);
        Post newPost = postRepository.save(post);

        // konwersja modelu do dto
        return mapToDTO(newPost);
    }

    @Override
    @Cacheable("postCache")
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        log.info("Sorting posts");
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // tworzenie instancji pageable
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        // pobranie zawartości stron dla obiektu pageable
        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content= listOfPosts.stream().map(this::mapToDTO).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    @Cacheable(value = "postCache", key = "#id")
    public PostDto getPostById(long id) {
        log.info("get post by id : {}", id);
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        // pobranie postu wg id z bazy
        log.info("updating post by id : {}", id);
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);
        Post updatedPost = postRepository.save(post);

        return mapToDTO(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        // pobranie postu wg id z bazy
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
    }

    @Override
    @Cacheable(value = "postCache", key = "#categoryId")
    public List<PostDto> getPostsByCategory(Long categoryId) {
        log.info("get category by id : {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        log.info("get list of posts by category id : {}", categoryId);
        List<Post> posts = postRepository.findByCategoryId(categoryId);

        return posts
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // konwersja modelu na obiekt dto za pomocą mappera
    private PostDto mapToDTO(Post post){
        return mapper.map(post, PostDto.class);
    }

    // konwersja dto na model za pomocą mappera
    private Post mapToEntity(PostDto postDto){
        return mapper.map(postDto, Post.class);
    }
}
