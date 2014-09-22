package blog.dao;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import blog.entity.BlogPost;
import blog.services.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public enum BlogPostDAO {
	INSTANCE;

	public List<BlogPost> getBlogPosts() {
		List<BlogPost> BlogPosts;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(BlogPost.class);
		try {
			BlogPosts = (List<BlogPost>) query.execute();
		} finally {
			pm.close();
		}
		return (BlogPosts);
	}

	public void deleteAll() {
		List<BlogPost> BlogPosts;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(BlogPost.class);
		try {
			BlogPosts = (List<BlogPost>) query.execute();
			pm.deletePersistentAll(BlogPosts);
		} finally {
			pm.close();
		}
	}

	public String add(String id, String subject, String body) {
		Key key = KeyFactory.createKey(BlogPost.class.getSimpleName(), id);
		BlogPost newPost = new BlogPost();

		newPost.setId(key);
		newPost.setSubject(subject);
		newPost.setBody(body);

		synchronized (this) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(newPost);
			} finally {
				pm.close();
			}
		}
		return (newPost.getId().getName());
	}

	public void update(String id, String subject, String body) {
		BlogPost post;
		synchronized (this) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Key k = KeyFactory.createKey(BlogPost.class.getSimpleName(),
					id);
			try {
				post = pm.getObjectById(BlogPost.class, k);
				post.setSubject(subject);
				post.setBody(body);
			} finally {
				pm.close();
			}
		}
	}

	public void remove(String idNumber) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k = KeyFactory
				.createKey(BlogPost.class.getSimpleName(), idNumber);
		BlogPost post;
		try {
			post = pm.getObjectById(BlogPost.class, k);
			pm.deletePersistent(post);
		} finally {
			pm.close();
		}
	}

	public BlogPost getBlogPost(String idNumber) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k = KeyFactory
				.createKey(BlogPost.class.getSimpleName(), idNumber);
		BlogPost post = null;
		try {
			post = pm.getObjectById(BlogPost.class, k);
		} finally {
			pm.close();
		}
		return post;
	}
}