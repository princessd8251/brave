package com.github.kristofa.brave;

import com.github.kristofa.brave.internal.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Parses binary annotation/tags based for a given input type. Implemented as a list of key and
 * value parsers
 */
public interface TagExtractor<T> {

  /**
   * Builders will typically implement this, so that configuration can be applied generically. For
   * example, all http instrumentation would receive the same configuration callbacks.
   */
  interface Config<T extends Config<T>> {
    /** A key that the user wants to add to a span */
    T addKey(String key);

    /** Overrides behavior of value conversion, or adds support for new ones. */
    T addValueParserFactory(ValueParserFactory factory);
  }

  /**
   * Implement this to teach TagExtractor how to parse a key, potentially across multiple
   * libraries.
   */
  interface ValueParserFactory {
    /**
     * Returns a parser that can handle the input type and the binary annotation key or null if
     * unsupported
     */
    ValueParser<?> create(Type type, String key);
  }

  /** Used to get binary annotation values from an input, such as a request object */
  interface ValueParser<T> {
    @Nullable String parse(T input);
  }

  /**
   * any non-null values parsed from the configured keys are returned
   */
  Collection<KeyValueAnnotation> extractTags(T input);
}