--==========================
-- TEST TRIGGERS: CL_ADDRT:
--==========================

-------------
-- EXISTS: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_id char(10);
	DECLARE v_src_id char(10);
	DECLARE v_op     char(1);
	DECLARE v_chg    char(1);

	SET v_tgt_id = 'AaiE5jP0Bi';
	SET v_src_id = 'AaO68nK09t'; -- test re-insert
	
	UPDATE CWSINT.CL_ADDRT r
	SET 
	    r.HOMLES_IND   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.HOMLES_IND FROM CWSRS1.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

	DELETE FROM CWSINT.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.HOMLES_IND FROM CWSRS1.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.CL_ADDRT (IDENTIFIER, ADDR_TPC, BK_INMT_ID, EFF_END_DT, EFF_STRTDT, LST_UPD_ID, LST_UPD_TS, FKADDRS_T, FKCLIENT_T, HOMLES_IND, FKREFERL_T)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		ADDR_TPC, BK_INMT_ID, EFF_END_DT, EFF_STRTDT, LST_UPD_ID, LST_UPD_TS, FKADDRS_T, FKCLIENT_T, HOMLES_IND, FKREFERL_T
	FROM CWSINT.CL_ADDRT x
	WHERE x.IDENTIFIER = v_src_id ;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.HOMLES_IND FROM CWSRS1.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert existing';
	END IF;

END;


rollback;


-------------
-- NEW: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_id  char(10);
	DECLARE v_src_id  char(10);
	DECLARE v_op      char(1);
	DECLARE v_chg     char(1);

	SET v_src_id = 'AaiE5jP0Bi';
	SET v_tgt_id = 'M9p3sGJXXX';
	
	INSERT INTO CWSINT.CL_ADDRT (IDENTIFIER, ADDR_TPC, BK_INMT_ID, EFF_END_DT, EFF_STRTDT, LST_UPD_ID, LST_UPD_TS, FKADDRS_T, FKCLIENT_T, HOMLES_IND, FKREFERL_T)
	SELECT 
	    v_tgt_id as IDENTIFIER,
		ADDR_TPC, BK_INMT_ID, EFF_END_DT, EFF_STRTDT, LST_UPD_ID, LST_UPD_TS, FKADDRS_T, FKCLIENT_T, HOMLES_IND, FKREFERL_T
	FROM CWSINT.CL_ADDRT x
	WHERE x.IDENTIFIER = v_src_id ;
		
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.HOMLES_IND FROM CWSRS1.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert new';
	END IF;

	UPDATE CWSINT.CL_ADDRT r
	SET 
	    r.HOMLES_IND   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.HOMLES_IND FROM CWSRS1.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update new';
	END IF;

	DELETE FROM CWSINT.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.HOMLES_IND FROM CWSRS1.CL_ADDRT r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete new';
	END IF;

END;


rollback;




