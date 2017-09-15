-- TRUNCATE TABLE postgres.public.intake_lov_code CASCADE ;

-- DROP TABLE intake_lov_code CASCADE ;

CREATE TABLE intake_lov_code (
	cat_id		      int4	  NOT NULL,
	lg_sys_id		  int4	  NOT NULL,
	intake_code       varchar NOT NULL,
	intake_display    varchar NOT NULL,
	omit_ind		  boolean NOT NULL,
	parent_lg_sys_id  int4,
	PRIMARY KEY (cat_id, lg_sys_id, intake_code)
);


ALTER TABLE intake_lov_code ADD FOREIGN KEY (cat_id)           REFERENCES intake_lov_category(cat_id) ;
ALTER TABLE intake_lov_code ADD FOREIGN KEY (lg_sys_id)        REFERENCES system_codes       ("id");
ALTER TABLE intake_lov_code ADD FOREIGN KEY (parent_lg_sys_id) REFERENCES system_codes       ("id");


GRANT SELECT ON intake_lov_code to PUBLIC;
