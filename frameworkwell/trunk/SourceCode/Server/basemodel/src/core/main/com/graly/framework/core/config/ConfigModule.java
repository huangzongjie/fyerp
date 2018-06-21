package com.graly.framework.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;



public abstract class ConfigModule
	implements Serializable, Initializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private transient Config config;

  private String identifier;
  private String searchClass;

  public ConfigModule() {
  }

  public void setConfig(Config config) {
  	this.config = config;
  }

  public Config getConfig() {
  	return config;
  }

  public void init() throws InitException {
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
  	in.defaultReadObject();
  }

  /**
   * Accessor method for property {@link #identifier}.
   * @return {@link #identifier}.
   */
  public String getIdentifier()
  {
    return identifier;
  }

  /**
   * Accessor method for property {@link #identifier}.
   * @param identifier The new value.
   */
  public void setIdentifier(String identifier)
  {
  	if ("null".equals(identifier))
  		throw new IllegalArgumentException("null is a reserved word and should not be used as identifier!");
    this.identifier = identifier;
  }

  /**
   * Accessor method for property {@link #searchClass}.
   * <p>
   * // TODO Marco: Who has added this? It seems to me, the work isn't complete!
   *
   * @return {@link #searchClass}.
   */
  public String getSearchClass()
  {
    return searchClass;
  }

  /**
   * Accessor method for property {@link #searchClass}.
   * @param searchClass The new value.
   */
  public void setSearchClass(String searchClass)
  {
  	if ("null".equals(searchClass))
  		throw new IllegalArgumentException("null is a reserved word and should not be used as searchClassName!");
    this.searchClass = searchClass;
  }

	/**
	 * Warning: The member config is transient and will be null at the clone!
	 * <p>
	 * The implementation of <code>clone()</code> in <code>ConfigModule</code> uses {@link Util#cloneSerializable(Object)}
	 * to create the copy. Hence, it will be a deep (and pretty complete) copy.
	 * </p>
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ConfigModule clone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			out.close();

			ObjectInputStream in = new ClassLoaderObjectInputStream(
					new ByteArrayInputStream(buf.toByteArray()), this.getClass().getClassLoader()
			);
			Object n = in.readObject();
			in.close();
			return (ConfigModule) n;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * An {@link ObjectInputStream} instance that uses the given
	 * {@link ClassLoader} to resolve classes that are to be deserialized.
	 * @author Marc Klinger - marc[at]nightlabs[dot]de
	 */
	private static class ClassLoaderObjectInputStream extends ObjectInputStream
	{
		private ClassLoader classLoader;
		public ClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
			super(in);
			this.classLoader = classLoader;
		}
		/* (non-Javadoc)
		 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
		 */
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc)
				throws IOException, ClassNotFoundException
		{
			if(classLoader == null)
				return super.resolveClass(desc);
			String name = desc.getName();
			try {
			    return Class.forName(name, false, classLoader);
			} catch (ClassNotFoundException ex) {
				return super.resolveClass(desc);
			}
		}
	}

}
