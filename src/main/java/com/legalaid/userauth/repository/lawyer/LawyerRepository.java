package com.legalaid.userauth.repository.lawyer;

import com.legalaid.userauth.entity.lawyer.LawyerProfile;
import com.legalaid.userauth.repository.lawyer.projection.LawyerDetailsProjectionForAdmin;
import com.legalaid.userauth.repository.lawyer.projection.LawyerProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface LawyerRepository extends JpaRepository<LawyerProfile, UUID> {

        Optional<LawyerProfile> findByBarNumber(String barNumber);

        @Query(value = """
    SELECT
        lp.bar_number AS barNumber,
        lp.bio AS bio,
        lp.specializations AS specializations,
        lp.years_experience AS yearsExperience,
        lp.is_verified AS isVerified,
        COALESCE(
            (
                SELECT jsonb_agg(
                    jsonb_build_object(
                        'id', lc.id,
                        'title', lc.title,
                        'credentialType', lc.credential_type,
                        'issuingBody', lc.issuing_body,
                        'url', lc.document_url,
                        'issuedDate', lc.issued_date,
                        'expiryDate', lc.expiry_date
                    )
                )
                FROM lawyer_credentials lc
                WHERE lc.lawyer_id = lp.id
            ),
            CAST('[]' AS jsonb)
        ) AS credentials
    FROM lawyer_profiles lp
    WHERE lp.id = :lawyerId
""", nativeQuery = true)
        LawyerProfileProjection findLawyerProfileById(UUID lawyerId);


        @Query(value = """
        SELECT \s
        u.profile_pic_url AS profilePicUrl,
        u.full_name AS fullName,
        u.email AS email,
        lp.id AS id,
        lp.specializations AS specializations,
        lp.years_experience AS experience,
        lp.created_at AS applied,
        lp.is_verified AS isVerified,
        lp.verified_at AS verifiedAt,
        lp.verified_by AS verifiedBy,
        COALESCE(
            (
                SELECT jsonb_agg(
                    jsonb_build_object(
                        'credentialType', lc.credential_type,
                        'title', lc.title,
                        'issuingBody', lc.issuing_body,
                        'issuedDate', lc.issued_date,
                        'expiryDate', lc.expiry_date,
                        'url', lc.document_url
                    )
                )
                FROM lawyer_credentials lc
                WHERE lc.lawyer_id = lp.id
            ), CAST('[]' AS jsonb)
        ) AS documents
        FROM lawyer_profiles lp
        LEFT JOIN users u ON u.id = lp.id
        WHERE lp.status = CAST(:status AS lawyer_status)
""", nativeQuery = true)
        List<LawyerDetailsProjectionForAdmin> findAllLawyerDetailsForAdmin(String status);

}
