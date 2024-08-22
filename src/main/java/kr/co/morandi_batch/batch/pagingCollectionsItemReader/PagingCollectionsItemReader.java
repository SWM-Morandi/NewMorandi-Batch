package kr.co.morandi_batch.batch.pagingCollectionsItemReader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.batch.item.database.orm.JpaQueryProvider;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@Slf4j
public class PagingCollectionsItemReader<T, C extends Collection<T>> extends AbstractPagingItemReader<C> {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private final Map<String, Object> jpaPropertyMap = new HashMap();
    private String queryString;
    private JpaQueryProvider queryProvider;
    private Map<String, Object> parameterValues;
    private boolean transacted = true;
    private Supplier<C> collectionFactory;
    private int collectionLength = 10;
    private int totalPageSize = 10;



    public int getTotalPageSize() {
        return totalPageSize;
    }

    public void setTotalPageSize(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }

    public PagingCollectionsItemReader() {
        this.setName(ClassUtils.getShortName(PagingCollectionsItemReader.class));
    }

    public void setCollectionFactory(Supplier<C> collectionFactory) {
        this.collectionFactory = collectionFactory;
    }

    private Query createQuery() {
        return this.queryProvider == null ? this.entityManager.createQuery(this.queryString) : this.queryProvider.createQuery();
    }
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public int getCollectionLength() {
        return collectionLength;
    }

    public void setCollectionLength(int collectionLength) {
        this.collectionLength = collectionLength;
    }

    public void setParameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (this.queryProvider == null) {
            Assert.state(this.entityManagerFactory != null, "EntityManager is required when queryProvider is null");
            Assert.state(StringUtils.hasLength(this.queryString), "Query string is required when queryProvider is null");
        }
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setQueryProvider(JpaQueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }

    protected void doOpen() throws Exception {
        super.doOpen();
        this.entityManager = this.entityManagerFactory.createEntityManager(this.jpaPropertyMap);
        if (this.entityManager == null) {
            throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
        } else {
            if (this.queryProvider != null) {
                this.queryProvider.setEntityManager(this.entityManager);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
    protected void doReadPage() {
        /*
        * TotalPageSize는 collectionLengh * pageSize
        * setMaxResult에 이걸 설정해서 한 번에 가져올 page 수를 설정
        *
        *  ex)collectionLength 는 50, chunkSize가 10이면
        *
        * 500개씩 페이징해오고, 50개씩 묶어서 반환
        *
        * */
        Query query = this.createQuery()
                        .setFirstResult(this.getPage() * this.getTotalPageSize())
                        .setMaxResults(this.getTotalPageSize());


        if (this.parameterValues != null) {
            Iterator iter = this.parameterValues.entrySet().iterator();

            while(iter.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)iter.next();
                query.setParameter((String)entry.getKey(), entry.getValue());
            }
        }
        initResults();

        fetchQuery(query);

    }
    protected void initResults() {
        if (CollectionUtils.isEmpty(results)) {
            results = new CopyOnWriteArrayList<>();
        } else {
            results.clear();
        }
    }

    protected void fetchQuery(Query query) {
        List<T> resultList = query.getResultList();
        if (!this.transacted) {
            resultList.forEach(entityManager::detach);
        }
        distributeResultsByChunk(resultList);
    }
    protected void distributeResultsByChunk(List<T> resultList) {
        for (int start = 0; start < resultList.size(); start += getCollectionLength()) {
            int end = Math.min(start + getCollectionLength(), resultList.size());
            List<T> subList = resultList.subList(start, end);

            C chunkCollection = this.collectionFactory.get();
            chunkCollection.addAll(subList);

            this.results.add(chunkCollection);
        }

    }
    protected void doClose() throws Exception {
        if(this.entityManager != null) {
            this.entityManager.close();
        }
        super.doClose();
    }
}
