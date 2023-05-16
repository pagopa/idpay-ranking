package it.gov.pagopa.ranking.repository;

import it.gov.pagopa.ranking.dto.controller.RankingRequestFilter;
import it.gov.pagopa.ranking.model.OnboardingRankingRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class OnboardingRankingRequestsRepositoryExtendedImpl implements OnboardingRankingRequestsRepositoryExtended {

    private final MongoTemplate mongoTemplate;

    public OnboardingRankingRequestsRepositoryExtendedImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<OnboardingRankingRequests> findAllBy(String initiativeId, RankingRequestFilter filter, Pageable pageable) {
        Query query = new Query().addCriteria(getCriteria(initiativeId, filter));
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        // Count objects
        long count = mongoTemplate.count(query, OnboardingRankingRequests.class);
        // Find List
        List<OnboardingRankingRequests> onboardingRankingRequests = mongoTemplate.find(query.with(pageable), OnboardingRankingRequests.class);
        // Convert in pageable
        return new PageImpl<>(onboardingRankingRequests, pageable, count);
    }

    private Criteria getCriteria(String initiativeId, RankingRequestFilter filters) {
        Criteria criteria = Criteria
                .where(OnboardingRankingRequests.Fields.initiativeId).is(initiativeId);

        // if filters are set, update the criteria; else, use default query
        updateCriteriaWithFilters(criteria, filters);
        return criteria;
    }

    private void updateCriteriaWithFilters(Criteria criteria, RankingRequestFilter filters) {
        if (filters != null){
            List<Criteria> criteriaList = new ArrayList<>();

            // beneficiaryRankingStatus
            if (filters.getBeneficiaryRankingStatus() != null) {
                criteriaList.add(Criteria.where(OnboardingRankingRequests.Fields.beneficiaryRankingStatus).is(filters.getBeneficiaryRankingStatus()));
            }
            // userId
            if (filters.getUserId() != null) {
                criteriaList.add(Criteria.where(OnboardingRankingRequests.Fields.userId).is(filters.getUserId()));
            }

            //add all criteria
            if(!criteriaList.isEmpty()) {
                criteria.andOperator(criteriaList);
            }
        }
    }
}
