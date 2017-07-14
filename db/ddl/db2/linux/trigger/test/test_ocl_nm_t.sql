--==========================
-- TEST TRIGGERS: OCL_NM_T:
--==========================

-------------
-- EXISTS: 
-------------

BEGIN ATOMIC

	DECLARE v_tgt_fkclient char(10);
	DECLARE v_tgt_third_id char(10);

	DECLARE v_src_fkclient char(10);
	DECLARE v_src_third_id char(10);

	DECLARE v_op     char(1);
	DECLARE v_chg    char(1);

	SET v_tgt_fkclient = 'AapJGAU04Z';
	SET v_tgt_third_id = 'T6g4R8604Z';
	
	SET v_src_fkclient = 'Abi5qGw04Z';
	SET v_src_third_id = 'Coybyba04Z';
	
	UPDATE CWSINT.OCL_NM_T r
	SET 
	    r.SAME_HM_CD   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.FKCLIENT_T = v_tgt_fkclient AND r.THIRD_ID = v_tgt_third_id;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update existing';
	END IF;

	DELETE FROM CWSINT.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete existing';
	END IF;

	INSERT INTO CWSINT.OCL_NM_T (THIRD_ID, FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, NMPRFX_DSC, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS)
	SELECT 
	    v_tgt_third_id as THIRD_ID, v_tgt_fkclient as FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, NMPRFX_DSC, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS, FKCLIENT_T
	FROM CWSINT.OCL_NM_T x
	WHERE r.FKCLIENT_T = v_src_fkclient AND r.THIRD_ID = v_src_third_id;
	
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id);
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

	SET v_tgt_id = 'Bieber6XXX';
	SET v_src_id = 'AattSTp01T'; -- test re-insert
	
	INSERT INTO CWSINT.OCL_NM_T (THIRD_ID, FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, NMPRFX_DSC, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS)
	SELECT 
	    v_tgt_third_id as THIRD_ID, v_tgt_fkclient as FKCLIENT_T, FIRST_NM, LAST_NM, MIDDLE_NM, NMPRFX_DSC, NAME_TPC, SUFX_TLDSC, LST_UPD_ID, LST_UPD_TS, FKCLIENT_T
	FROM CWSINT.OCL_NM_T x
	WHERE r.FKCLIENT_T = v_src_fkclient AND r.THIRD_ID = v_src_third_id;
		
	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'I') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: insert new';
	END IF;

	UPDATE CWSINT.OCL_NM_T r
	SET 
	    r.SAME_HM_CD   = 'Y',
	    r.LST_UPD_ID   = '0x5',
	    r.LST_UPD_TS   = current timestamp
	WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'U' OR v_chg != 'Y') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: update new';
	END IF;

	DELETE FROM CWSINT.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id ;

	SET (v_op, v_chg) = (SELECT r.IBMSNAP_OPERATION, r.SAME_HM_CD FROM CWSRS1.OCL_NM_T r WHERE r.IDENTIFIER = v_tgt_id);
	IF (v_op != 'D') THEN
		SIGNAL SQLSTATE '70002'
		SET MESSAGE_TEXT = 'Failed: delete new';
	END IF;

END;


rollback;




