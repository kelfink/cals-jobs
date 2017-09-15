CREATE TABLE intake_only_lov_code (
	cat_id		      int4	  NOT NULL,
	intake_code       varchar NOT NULL,
	intake_display    varchar NOT NULL,
	omit_ind		  boolean NOT NULL,
	PRIMARY KEY (cat_id, intake_code)
);


ALTER TABLE intake_only_lov_code ADD FOREIGN KEY (cat_id) REFERENCES intake_only_lov_category(cat_id) ;


GRANT SELECT ON intake_only_lov_code to PUBLIC;
