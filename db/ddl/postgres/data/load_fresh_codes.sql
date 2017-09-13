
-- INITIAL STEPS: truncate and load intake_raw_codes.

--TRUNCATE TABLE postgres.public.intake_raw_codes CASCADE ;
--commit;

TRUNCATE TABLE postgres.public.intake_lov_category CASCADE ;
commit;

TRUNCATE TABLE postgres.public.intake_lov_code CASCADE ;
commit;

TRUNCATE TABLE postgres.public.intake_only_lov_category CASCADE ;
commit;

TRUNCATE TABLE postgres.public.intake_only_lov_code CASCADE ;
commit;


-- LEGACY CATEGORIES:
-- No parent category:

INSERT INTO intake_lov_category (lg_meta, intake_type, use_log_id, cat_descr)
select distinct lg_meta, intake_type, use_log_id, 'fill me ' as cat_descr
from intake_raw_codes r
where parent_intake_type is null or length(trim(parent_intake_type)) = 0
order by 1,2;


-- With parent category:

INSERT INTO intake_lov_category (lg_meta, intake_type, parent_cat_id, use_log_id, cat_descr)
select distinct r.lg_meta, r.intake_type, cat.cat_id, r.use_log_id, 'fill me ' as cat_descr
from intake_raw_codes r
join intake_lov_category cat on cat.intake_type = trim(r.parent_intake_type)
where parent_intake_type is not null or length(trim(parent_intake_type)) > 0
order by 1,2;

commit;



-- LEGACY CODES:
-- Parent code or no child codes:

insert into intake_lov_code (cat_id, lg_sys_id, intake_code, intake_display, omit_ind)
select c.cat_id, r.lg_sys_id, r.intake_code, r.intake_display, r.omit_ind
from intake_raw_codes r
join intake_lov_category c on c.lg_meta = r.lg_meta and c.intake_type = r.intake_type
WHERE (r.lg_inactive is null or r.lg_inactive = 'N')
AND (r.parent_intake_type is null or length(r.parent_intake_type) = 0)
AND r.intake_code is not null 
AND r.intake_display is not null 
ORDER BY 1,2;


-- Child codes:

insert into intake_lov_code (cat_id, lg_sys_id, parent_lg_sys_id, intake_code, intake_display, omit_ind)
select c.cat_id, r.lg_sys_id, r.parent_sys_id, r.intake_code, r.intake_display, r.omit_ind
from intake_raw_codes r
join intake_lov_category c on c.lg_meta = r.lg_meta and c.intake_type = r.intake_type
left join intake_lov_category cp on cp.intake_type = r.parent_intake_type
WHERE (r.lg_inactive is null or r.lg_inactive = 'N')
AND (r.parent_intake_type is not null and length(r.parent_intake_type) > 0)
AND r.parent_sys_id is not null
AND r.intake_code is not null 
AND r.intake_display is not null 
ORDER BY 1,2;

commit;



INSERT INTO postgres.public.intake_only_lov_category (intake_type, cat_descr) VALUES ('contact_status', 'contact status');
commit;


INSERT INTO postgres.public.intake_only_lov_code (cat_id, intake_code, intake_display, omit_ind) VALUES (1, 'S', 'Scheduled', false);
INSERT INTO postgres.public.intake_only_lov_code (cat_id, intake_code, intake_display, omit_ind) VALUES (1, 'C', 'Completed', false);
INSERT INTO postgres.public.intake_only_lov_code (cat_id, intake_code, intake_display, omit_ind) VALUES (1, 'A', 'Attempted', false);
commit;












