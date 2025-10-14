package fr.insee.vtl.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/** <code>Structured</code> is the base interface for representing structured data. */
public interface Structured {

  /**
   * Returns the structure associated to the data as a list of structure components.
   *
   * @return The structure associated to the data as a list of structure components.
   */
  DataStructure getDataStructure();

  /**
   * Returns the list of column names.
   *
   * @return The column names as a list of strings.
   */
  default List<String> getColumnNames() {
    return new ArrayList<>(getDataStructure().keySet());
  }

  /**
   * Returns the map of column names & roles.
   *
   * @return The column names & roles.
   */
  default Map<String, Dataset.Role> getRoles() {
    return getDataStructure().values().stream()
        .collect(Collectors.toMap(Component::getName, Component::getRole));
  }

  default List<Component> getIdentifiers() {
    return getDataStructure().getIdentifiers();
  }

  default List<Component> getMeasures() {
    return getDataStructure().getMeasures();
  }

  default List<Component> getAttributes() {
    return getDataStructure().getAttributes();
  }

  default Boolean isMonoMeasure() {
    return getDataStructure().isMonoMeasure();
  }

  default List<String> getIdentifierNames() {
    return getIdentifiers().stream().map(Component::getName).toList();
  }

  default List<String> getMeasureNames() {
    return getMeasures().stream().map(Component::getName).toList();
  }

  /**
   * The <code>Structure</code> class represent a structure component with its name, type, role and
   * nullable.
   */
  class Component implements Serializable {

    private final String name;
    private final Class<?> type;
    private final Dataset.Role role;
    private final Boolean nullable;

    private final String valuedomain;

    /**
     * Constructor taking the name, type and role of the component.
     *
     * @param name A string giving the name of the structure component to create
     * @param type A <code>Class</code> giving the type of the structure component to create
     * @param role A <code>Role</code> giving the role of the structure component to create
     */
    public Component(String name, Class<?> type, Dataset.Role role) {
      this.name = requireNonNull(name);
      this.type = requireNonNull(type);
      this.role = requireNonNull(role);
      this.nullable = buildNullable(null, role);
      this.valuedomain = null;
    }

    /**
     * Constructor taking the name, type and role of the component.
     *
     * @param name A string giving the name of the structure component to create
     * @param type A <code>Class</code> giving the type of the structure component to create
     * @param role A <code>Role</code> giving the role of the structure component to create
     * @param nullable A <code>Nullable</code> giving the nullable of the structure component to
     *     create
     * @param valuedomain A <code>Valuedomain</code> giving the valuedomain of the structure
     *     component to create
     */
    public Component(
        String name, Class<?> type, Dataset.Role role, Boolean nullable, String valuedomain) {
      this.name = requireNonNull(name);
      this.type = requireNonNull(type);
      this.role = requireNonNull(role);
      this.nullable = buildNullable(nullable, role);
      this.valuedomain = valuedomain;
    }

    /**
     * Constructor taking the name, type, role and nullable of the component.
     *
     * @param name A string giving the name of the structure component to create
     * @param type A <code>Class</code> giving the type of the structure component to create
     * @param role A <code>Role</code> giving the role of the structure component to create
     * @param nullable A <code>Nullable</code> giving the nullable of the structure component to
     *     create
     */
    public Component(String name, Class<?> type, Dataset.Role role, Boolean nullable) {
      this.name = requireNonNull(name);
      this.type = requireNonNull(type);
      this.role = requireNonNull(role);
      this.nullable = buildNullable(nullable, role);
      this.valuedomain = null;
    }

    /**
     * Constructor taking an existing component.
     *
     * @param component The component to copy.
     */
    public Component(Component component) {
      this.name = component.getName();
      this.type = component.getType();
      this.role = component.getRole();
      this.nullable = component.getNullable();
      this.valuedomain = component.getValuedomain();
    }

    /**
     * Refines the nullable attribute of a <code>Component</code> regarding its role.
     *
     * @param initialNullable The dataset nullable attribute.
     * @param role The role of the component as a value of the <code>Role</code> enumeration
     * @return A boolean which is <code>true</code> if the component values can be null, <code>false
     * </code> otherwise.
     */
    private Boolean buildNullable(Boolean initialNullable, Dataset.Role role) {
      if (role.equals(Dataset.Role.IDENTIFIER)) return false;
      if (initialNullable == null) return true;
      return initialNullable;
    }

    /**
     * Tests if a component is an identifier.
     *
     * @return <code>true</code> if the component is an identifier, <code>false</code>.
     */
    public boolean isIdentifier() {
      return Dataset.Role.IDENTIFIER.equals(this.role);
    }

    /**
     * Tests if a component is a measure.
     *
     * @return <code>true</code> if the component is a measure, <code>false</code>.
     */
    public boolean isMeasure() {
      return Dataset.Role.MEASURE.equals(this.role);
    }

    /**
     * Tests if a component is an attribute.
     *
     * @return <code>true</code> if the component is an attribute, <code>false</code>.
     */
    public boolean isAttribute() {
      return Dataset.Role.ATTRIBUTE.equals(this.role);
    }

    /**
     * Returns the name of the component.
     *
     * @return The name of the component as a string.
     */
    public String getName() {
      return name;
    }

    /**
     * Returns the type of the component.
     *
     * @return The type of the component as an instance of <code>Class</code>
     */
    public Class<?> getType() {
      return type;
    }

    /**
     * Returns the role of component.
     *
     * @return The role of the component as a value of the <code>Role</code> enumeration
     */
    public Dataset.Role getRole() {
      return role;
    }

    /**
     * Returns the nullable of component.
     *
     * @return The nullable of the component as a Boolean
     */
    public Boolean getNullable() {
      return nullable;
    }

    /**
     * Returns the valuedomain of component.
     *
     * @return The valuedomain of the component as a String
     */
    public String getValuedomain() {
      return valuedomain;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Component component = (Component) o;
      return name.equals(component.name) && type.equals(component.type) && role == component.role;
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type, role);
    }

    @Override
    public String toString() {
      return "Component{" + name + ", type=" + type + ", role=" + role + '}';
    }
  }

  /**
   * The <code>DataStructure</code> represents the structure of a Dataset.
   *
   * <p>A DataStructure helps with the indexing of a {@link DataPoint}. It keeps the position of
   * each component.
   */
  class DataStructure extends IndexedHashMap<String, Structured.Component> {

    /**
     * Creates a DataStructure with type and role maps.
     *
     * @param types The types of each component, by name
     * @param roles The roles of each component, by name
     * @throws IllegalArgumentException if the key set of types and roles are not equal.
     */
    public DataStructure(Map<String, Class<?>> types, Map<String, Dataset.Role> roles) {
      super(types.size());
      if (!types.keySet().equals(roles.keySet())) {
        throw new IllegalArgumentException("type and roles key sets inconsistent");
      }
      for (String column : types.keySet()) {
        Component component = new Component(column, types.get(column), roles.get(column));
        put(column, component);
      }
    }

    /**
     * Creates a DataStructure with type, role and nullable maps.
     *
     * @param types The types of each component, by name
     * @param roles The roles of each component, by name
     * @param nullables The nullables of each component, by name
     * @throws IllegalArgumentException if the key set of types and roles are not equal.
     */
    public DataStructure(
        Map<String, Class<?>> types,
        Map<String, Dataset.Role> roles,
        Map<String, Boolean> nullables) {
      super(types.size());
      if (!types.keySet().equals(roles.keySet())) {
        throw new IllegalArgumentException("type and roles key sets inconsistent");
      }
      for (String column : types.keySet()) {
        Component component =
            new Component(column, types.get(column), roles.get(column), nullables.get(column));
        put(column, component);
      }
    }

    /**
     * Creates a DataStructure with a collection of components.
     *
     * @param components A collection of components
     * @throws IllegalArgumentException in case of duplicate column names
     */
    public DataStructure(Collection<Component> components) {
      super(components.size());
      Set<Component> duplicates = new HashSet<>();
      for (Component component : components) {
        var newComponent = new Component(component);
        var old = put(newComponent.getName(), newComponent);
        if (old != null) {
          duplicates.add(old);
        }
      }
      if (!duplicates.isEmpty()) {
        throw new IllegalArgumentException("duplicate column " + duplicates);
      }
    }

    // TODO: Remove. We can simply use a Map<String, Component> of the
    //        constructor with Collection<Component>
    public DataStructure(DataStructure dataStructure) {
      super(dataStructure);
    }

    public List<Component> getIdentifiers() {
      return values().stream().filter(Component::isIdentifier).collect(Collectors.toList());
    }

    public List<Component> getMeasures() {
      return values().stream().filter(Component::isMeasure).collect(Collectors.toList());
    }

    public List<Component> getAttributes() {
      return values().stream().filter(Component::isAttribute).collect(Collectors.toList());
    }

    public Map<String, Dataset.Role> getRoles() {
      return entrySet().stream()
          .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getRole()));
    }

    public List<Component> getByValuedomain(String valuedomain) {
      return values().stream()
          .filter(c -> valuedomain.equals(c.getValuedomain()))
          .collect(Collectors.toList());
    }

    public Boolean isMonoMeasure() {
      return getMeasures().size() == 1;
    }
  }

  /**
   * A structured row of a {@link Dataset}.
   *
   * <p>A point is composed of a structure and a list of values. Values can be accessed by position
   * or by name.
   *
   * <p>Two <code>DataPoint</code> instances are considered equal if all of their identifier values
   * are equal.
   */
  record DataPoint(DataStructure dataStructure, List<Object> objects) implements List<Object> {

      public DataPoint{
          requireNonNull(dataStructure);
      }

    /**
     * Create a new instance with the given {@link DataStructure} and a map of values.
     *
     * <p>Note that only the values associated with the columns of the data structure will be used.
     *
     * @param dataStructure the data structure
     * @param map the map of values
     */
    public DataPoint(DataStructure dataStructure, Map<String, Object> map) {

      this(dataStructure, map.entrySet().stream()
              .sorted(Map.Entry.comparingByKey(Comparator.comparing(dataStructure::indexOfKey)))
              .map(Map.Entry::getValue)
              .toList());
    }

    /**
     * Create an empty <code>DataPoint</code> with the given data structure
     *
     * @param dataStructure the data structure
     */
    public DataPoint(DataStructure dataStructure) {
      this(dataStructure, new Object[dataStructure.size()]);
    }

    /**
     * Create a new instance with the given {@link DataStructure} and a collection of values.
     *
     * <p>Note that only the values within the size of the data structure will be used, from 0 to
     * dataStructure.size() - 1.
     *
     * @param dataStructure the data structure
     * @param collection the collection of values
     */
    public DataPoint(DataStructure dataStructure, Collection<Object> collection) {
      super(dataStructure.size());
      this.dataStructure = requireNonNull(dataStructure);
      addAll(collection);
    }

    /**
     * Get the value by name.
     *
     * @param column the name of the column
     * @return the value associated with the column
     * @throws IndexOutOfBoundsException if the name is not in the {@link DataStructure}.
     */
    public Object get(String column) {
      return get(dataStructure.indexOfKey(column));
    }

    /**
     * Set the value by name.
     *
     * @param column the name of the column
     * @param object the name of the column
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the name is not in the {@link DataStructure}.
     */
    public Object set(String column, Object object) {
      int index = dataStructure.indexOfKey(column);
      if (index > size() - 1) {
        growSize(index + 1);
      }
      return set(index, object);
    }

      @Override
      public int size() {
          return 0;
      }

      @Override
      public boolean isEmpty() {
          return false;
      }

      @Override
      public boolean contains(Object o) {
          return false;
      }

      @Override
      public Iterator<Object> iterator() {
          return null;
      }

      @Override
      public Object[] toArray() {
          return new Object[0];
      }

      @Override
      public <T> T[] toArray(T[] a) {
          return null;
      }

      @Override
      public boolean add(Object o) {
          return false;
      }

      @Override
      public boolean remove(Object o) {
          return false;
      }

      @Override
      public boolean containsAll(Collection<?> c) {
          return false;
      }

      @Override
      public boolean addAll(Collection<?> c) {
          return false;
      }

      @Override
      public boolean addAll(int index, Collection<?> c) {
          return false;
      }

      @Override
      public boolean removeAll(Collection<?> c) {
          return false;
      }

      @Override
      public boolean retainAll(Collection<?> c) {
          return false;
      }

      @Override
      public void clear() {

      }

      @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DataPoint objects = (DataPoint) o;
      for (Component component : dataStructure.values()) {
        if (!Dataset.Role.IDENTIFIER.equals(component.getRole())) {
          continue;
        }
        if (!get(component.getName()).equals(objects.get(component.getName()))) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;
      for (Component component : dataStructure.values()) {
        if (!Dataset.Role.IDENTIFIER.equals(component.getRole())) {
          continue;
        }
        Object e = get(component.getName());
        hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
      }
      return hashCode;
    }

      @Override
      public Object get(int index) {
          return null;
      }

      @Override
      public Object set(int index, Object element) {
          return null;
      }

      @Override
      public void add(int index, Object element) {

      }

      @Override
      public Object remove(int index) {
          return null;
      }

      @Override
      public int indexOf(Object o) {
          return 0;
      }

      @Override
      public int lastIndexOf(Object o) {
          return 0;
      }

      @Override
      public ListIterator<Object> listIterator() {
          return null;
      }

      @Override
      public ListIterator<Object> listIterator(int index) {
          return null;
      }

      @Override
      public List<Object> subList(int fromIndex, int toIndex) {
          return List.of();
      }
  }

  /**
   * A {@link Map} <strong>view</strong> of a {@link DataPoint}.
   *
   * <p>The methods remove, putAll and clear are not supported.
   */
  class DataPointMap implements Map<String, Object> {

    private final DataPoint dataPoint;

    /**
     * Create a new <code>DataPointMap</code>.
     *
     * @param dataPoint the data point.
     */
    public DataPointMap(DataPoint dataPoint) {
      this.dataPoint = dataPoint;
    }

    @Override
    public int size() {
      return dataPoint.size();
    }

    @Override
    public boolean isEmpty() {
      return dataPoint.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return dataPoint.dataStructure.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return dataPoint.contains(value);
    }

    @Override
    public Object get(Object key) {
      return dataPoint.get((String) key);
    }

    @Override
    public Object put(String key, Object value) {
      return dataPoint.set(key, value);
    }

    /**
     * Unsupported operation.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public Object remove(Object key) {
      throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public void putAll(Map<? extends String, ?> m) {
      throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
      return dataPoint.dataStructure.keySet();
    }

    @Override
    public Collection<Object> values() {
      return dataPoint;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
      return dataPoint.dataStructure.keySet().stream()
          .map(component -> new AbstractMap.SimpleEntry<>(component, dataPoint.get(component)))
          .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Map)) return false;
      Map<?, ?> that = (Map<?, ?>) o;
      return entrySet().equals(that.entrySet());
    }

    @Override
    public int hashCode() {
      return Objects.hash(entrySet());
    }

    public String toString() {
      Iterator<Entry<String, Object>> i = entrySet().iterator();
      if (!i.hasNext()) return "{}";

      StringBuilder sb = new StringBuilder();
      sb.append('{');
      for (; ; ) {
        Entry<String, Object> e = i.next();
        String key = e.getKey();
        Object value = e.getValue();
        sb.append(key);
        sb.append('=');
        sb.append(value == this ? "(this Map)" : value);
        if (!i.hasNext()) return sb.append('}').toString();
        sb.append(',').append(' ');
      }
    }
  }
}
