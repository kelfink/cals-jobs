DROP TRIGGER CWSINT.trg_claddr_upd;

CREATE TRIGGER CWSINT.trg_claddr_upd
AFTER UPDATE ON CL_ADDRT
REFERENCING OLD AS OROW
            NEW AS NROW
FOR EACH ROW MODE DB2SQL
BEGIN ATOMIC
UPDATE CWSRS1.CL_ADDRT
SET 
	ADDR_TPC = nrow.ADDR_TPC,
	BK_INMT_ID = nrow.BK_INMT_ID,
	EFF_END_DT = nrow.EFF_END_DT,
	EFF_STRTDT = nrow.EFF_STRTDT,
	LST_UPD_ID = nrow.LST_UPD_ID,
	LST_UPD_TS = nrow.LST_UPD_TS,
	FKADDRS_T = nrow.FKADDRS_T,
	FKCLIENT_T = nrow.FKCLIENT_T,
	HOMLES_IND = nrow.HOMLES_IND,
	FKREFERL_T = nrow.FKREFERL_T,
	IBMSNAP_OPERATION = 'U',
	IBMSNAP_LOGMARKER = current timestamp
WHERE IDENTIFIER = nrow.identifier;
END


