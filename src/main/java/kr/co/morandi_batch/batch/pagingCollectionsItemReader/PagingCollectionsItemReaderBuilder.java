package kr.co.morandi_batch.batch.pagingCollectionsItemReader;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.orm.JpaQueryProvider;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class PagingCollectionsItemReaderBuilder<T, C extends Collection<T>> {
    private int pageSize = 10;
    private int collectionLength = 1;
    private EntityManagerFactory entityManagerFactory;
    private Map<String, Object> parameterValues;
    private boolean transacted = true;
    private String queryString;
    private JpaQueryProvider queryProvider;
    private boolean saveState = true;
    private String name;
    private int maxItemCount = Integer.MAX_VALUE;
    private int currentItemCount;
    private Supplier<C> collectionFactory;

    public PagingCollectionsItemReaderBuilder() {
    }

    public PagingCollectionsItemReaderBuilder<T, C> saveState(boolean saveState) {
        this.saveState = saveState;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> name(String name) {
        this.name = name;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> maxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> chunkAndCollectionSize(int chunkSize, int collectionLength) {
        this.pageSize = chunkSize;
        this.collectionLength = collectionLength;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> parameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> queryProvider(JpaQueryProvider queryProvider) {
        this.queryProvider = queryProvider;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> queryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> transacted(boolean transacted) {
        this.transacted = transacted;
        return this;
    }

    public PagingCollectionsItemReaderBuilder<T, C> collectionClass(Class<?> collectionClass) {
        this.collectionFactory = createCollectionSupplier(collectionClass);
        return this;
    }
    public <C extends Collection<?>> Supplier<C> createCollectionSupplier(Class<?> collectionClass) {
        return () -> {
            try {
                return (C) collectionClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("Cannot instantiate collection class: " + collectionClass.getName(), e);
            }
        };
    }

    public PagingCollectionsItemReaderBuilder<T, C> entityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        return this;
    }

    public PagingCollectionsItemReader<T, C> build() {
        Assert.isTrue(this.pageSize > 0, "pageSize must be greater than zero");
        Assert.isTrue(this.collectionLength > 0, "collectionLength must be greater than zero");
        Assert.notNull(this.entityManagerFactory, "An EntityManagerFactory is required");
        if (this.saveState) {
            Assert.hasText(this.name, "A name is required when saveState is set to true");
        }
        if (this.queryProvider == null) {
            Assert.hasLength(this.queryString, "Query string is required when queryProvider is null");
        }
        PagingCollectionsItemReader<T,C> reader = new PagingCollectionsItemReader<>();
        reader.setCollectionFactory(this.collectionFactory);
        reader.setQueryString(this.queryString);
        reader.setPageSize(this.pageSize);
        reader.setParameterValues(this.parameterValues);
        reader.setEntityManagerFactory(this.entityManagerFactory);
        reader.setQueryProvider(this.queryProvider);
        reader.setTransacted(this.transacted);
        reader.setCurrentItemCount(this.currentItemCount);
        reader.setMaxItemCount(this.maxItemCount);
        reader.setSaveState(this.saveState);
        reader.setName(this.name);
        reader.setTotalPageSize(this.pageSize * this.collectionLength);
        reader.setCollectionLength(this.collectionLength);
        return reader;
    }
}
