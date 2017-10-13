-- DROP VIEW VW_SCREENING_HISTORY ;

CREATE VIEW VW_SCREENING_HISTORY AS
 SELECT s.id AS screening_id,
    alg.id AS allegation_id,
    p.id AS participant_id,
    p.legacy_id AS person_legacy_id,
    alg.allegation_types,
        CASE
            WHEN ((p.id)::text = (alg.victim_id)::text) THEN 1
            ELSE NULL::integer
        END AS is_alg_victim,
        CASE
            WHEN ((p.id)::text = (alg.perpetrator_id)::text) THEN 1
            ELSE NULL::integer
        END AS is_alg_perp,
    s.referral_id,
    s.reference,
    s.started_at,
    s.ended_at,
    s.incident_date,
    s.location_type,
    s.communication_method,
    s.name AS screening_name,
    s.screening_decision,
    s.incident_county,
    s.report_narrative,
    s.assignee,
    s.additional_information,
    s.screening_decision_detail,
    s.indexable,
    p.date_of_birth AS birth_dt,    
    p.first_name,
    p.last_name,
    p.gender,
    p.ssn,
    p.roles,
        CASE
            WHEN ((array_position(p.roles, 'Mandated Reporter'::character varying) IS NOT NULL) OR (array_position(p.roles, 'Non-mandated Reporter'::character varying) IS NOT NULL) OR (array_position(p.roles, 'Anonymous Reporter'::character varying) IS NOT NULL)) THEN 1
            ELSE 0
        END AS is_reporter,
        CASE
            WHEN (((p.id)::text = (alg.victim_id)::text) OR (array_position(p.roles, 'Victim'::character varying) IS NOT NULL)) THEN 1
            ELSE 0
        END AS is_victim,
        CASE
            WHEN (((p.id)::text = (alg.perpetrator_id)::text) OR (array_position(p.roles, 'Perpetrator'::character varying) IS NOT NULL)) THEN 1
            ELSE 0
        END AS is_perpetrator,
    pa.id AS partc_addr_id,
    pa.participant_id AS pa_partc_id,
    adr.id AS address_id,
    adr.type AS address_type,
    adr.street_address,
    adr.city,
    adr.state,
    adr.zip,
    ppn.phone_number_id,
    ppn.participant_id AS ph_partc_id,
    ph.number AS phone_number,
    ph.type AS phone_type,
    ( SELECT max(v.created_at) AS max
           FROM versions v
          WHERE ((((v.item_type)::text = 'Participant'::text) AND ((p.id)::text = ((v.item_id)::character varying)::text)) OR (((v.item_type)::text = 'Address'::text) AND ((adr.id)::text = ((v.item_id)::character varying)::text)) OR (((v.item_type)::text = 'Allegation'::text) AND (alg.id = v.item_id)) OR (((v.item_type)::text = 'ParticipantAddress'::text) AND ((pa.id)::text = ((v.item_id)::character varying)::text)) OR (((v.item_type)::text = 'ParticipantPhoneNumber'::text) AND ((ppn.id)::text = ((v.item_id)::character varying)::text)) OR (((v.item_type)::text = 'PhoneNumber'::text) AND ((ph.id)::text = ((v.item_id)::character varying)::text)) OR (((v.item_type)::text = 'Screening'::text) AND ((s.id)::text = ((v.item_id)::character varying)::text)))) AS last_chg
   FROM ((((((screenings s
     LEFT JOIN allegations alg ON (((alg.screening_id)::text = (s.id)::text)))
     LEFT JOIN participants p ON ((((p.screening_id)::text = (s.id)::text) OR ((p.id)::text = (alg.victim_id)::text) OR ((p.id)::text = (alg.perpetrator_id)::text))))
     LEFT JOIN participant_addresses pa ON (((pa.participant_id)::text = (p.id)::text)))
     LEFT JOIN addresses adr ON ((((adr.id)::text = (pa.address_id)::text) AND (COALESCE(adr.street_address, adr.city, adr.zip) IS NOT NULL))))
     LEFT JOIN participant_phone_numbers ppn ON (((ppn.participant_id)::text = (p.id)::text)))
     LEFT JOIN phone_numbers ph ON (((ph.id)::text = (ppn.phone_number_id)::text))
);