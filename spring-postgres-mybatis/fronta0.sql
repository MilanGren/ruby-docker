
DO $$ DECLARE
  r RECORD;
BEGIN
  FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP
    EXECUTE 'DROP TABLE ' || quote_ident(r.tablename) || ' CASCADE';
  END LOOP;
END $$;

CREATE TABLE fronta0(
   no INT GENERATED ALWAYS AS IDENTITY,
   noderef VARCHAR(255) NOT NULL,
   edid VARCHAR(255) NOT NULL,
   davkaid VARCHAR(255) NOT NULL,
   status VARCHAR(255) NOT NULL,
   PRIMARY KEY(no)
);

	   
INSERT INTO fronta0(noderef, edid, davkaid, status)
VALUES('noderef0','edid0','davkaid1','NPROGRESS'), /*nejstarsi zaznam*/
      ('noderef1','edid1','davkaid1','WAITING'), 
      ('noderef2','edid3','davkaid2','WAITING');
      
      
SELECT * FROM fronta0 ;


