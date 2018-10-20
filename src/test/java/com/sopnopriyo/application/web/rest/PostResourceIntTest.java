package com.sopnopriyo.application.web.rest;

import com.sopnopriyo.application.SopnopriyoApp;

import com.sopnopriyo.application.domain.Post;
import com.sopnopriyo.application.repository.UserRepository;
import com.sopnopriyo.application.service.PostService;
import com.sopnopriyo.application.service.UserService;
import com.sopnopriyo.application.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static com.sopnopriyo.application.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sopnopriyo.application.domain.enumeration.Status;
/**
 * Test class for the PostResource REST controller.
 *
 * @see PostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SopnopriyoApp.class)
public class PostResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";


    private static final Status DEFAULT_STATUS = Status.DRAFT;
    private static final Status UPDATED_STATUS = Status.PUBLISHED;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

	@Autowired
	private UserRepository userRepository;

	@Autowired
    private PostService postService;

	@Autowired
	private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPostMockMvc;

    private Post post;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PostResource postResource = new PostResource(postService, userService);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
	@WithMockUser
    public Post createEntity(EntityManager em) {
        Post post = new Post()
            .title(DEFAULT_TITLE)
            .body(DEFAULT_BODY)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .status(DEFAULT_STATUS)
			.date(DEFAULT_DATE)
			.user(userRepository.findOneByLogin("user").get());
        return post;
    }

    @Before
    public void initTest() {
        post = createEntity(em);
    }

    @Test
	@Transactional
	@WithMockUser
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postService.findAll().size();

        // Create the Post
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPost.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testPost.getCoverImageUrl()).isEqualTo(DEFAULT_COVER_IMAGE_URL);
        assertThat(testPost.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPost.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
	@Transactional
	@WithMockUser
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postService.findAll().size();

        // Create the Post with an existing ID
        post.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = postService.findAll().size();
        // set the field null
        post.setTitle(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = postService.findAll().size();
        // set the field null
        post.setDate(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
	@Transactional
	@WithMockUser
    public void getAllPosts() throws Exception {
        // Initialize the database
        postService.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }


    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postService.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser
    public void updatePost() throws Exception {
        // Initialize the database
        postService.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postService.findAll().size();

        // Update the post
        Post updatedPost = postService.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPost.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testPost.getCoverImageUrl()).isEqualTo(UPDATED_COVER_IMAGE_URL);
        assertThat(testPost.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPost.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updatePostWithoutAuth() throws Exception {
        // Initialize the database
        postService.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postService.findAll().size();

        // Update the post
        Post updatedPost = postService.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isNotFound());

        assertThat(databaseSizeBeforeUpdate).isEqualTo(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postService.findAll().size();

        // Create the Post

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @WithMockUser
    @Test
    @Transactional
    public void updatePostByAdmin() throws Exception {
        // Initialize the database
        postService.saveAndFlush(post);

        int databaseSizeBeforeUpdate = postService.findAll().size();

        // Update the post
        Post updatedPost = postService.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .title(UPDATED_TITLE)
            .body(UPDATED_BODY)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE)
            .user(userRepository.findOneByLogin("admin").get());

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        assertThat(databaseSizeBeforeUpdate).isEqualTo(databaseSizeBeforeUpdate);
    }

    @Test
    @WithMockUser
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postService.saveAndFlush(post);

        int databaseSizeBeforeDelete = postService.findAll().size();

        // Get the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Post> postList = postService.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
        Post post1 = new Post();
        post1.setId(1L);
        Post post2 = new Post();
        post2.setId(post1.getId());
        assertThat(post1).isEqualTo(post2);
        post2.setId(2L);
        assertThat(post1).isNotEqualTo(post2);
        post1.setId(null);
        assertThat(post1).isNotEqualTo(post2);
    }
}
