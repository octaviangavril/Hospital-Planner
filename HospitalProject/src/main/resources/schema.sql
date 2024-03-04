BEGIN;

DO $$
DECLARE
  function_name RECORD;
BEGIN
  FOR function_name IN (SELECT proname FROM pg_proc WHERE pronamespace = 'public'::regnamespace)
  LOOP
    EXECUTE 'DROP FUNCTION IF EXISTS ' || function_name.proname || ' CASCADE';
  END LOOP;
END $$;

DROP TABLE IF EXISTS symptoms_patient;
DROP TABLE IF EXISTS symptoms_medical;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS prescription_medicines;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS medicines_stock;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS receptionists;
DROP TABLE IF EXISTS pharmacists;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS users;



CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
	password_salt VARCHAR(255) NOT NULL,
	role VARCHAR(255) NOT NULL
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;


CREATE TABLE receptionists (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    desk_id bigint,
	user_id bigint REFERENCES public.users (id)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.receptionists
    OWNER to postgres;


CREATE TABLE pharmacists (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    pharmacy VARCHAR(255),
	user_id bigint REFERENCES public.users (id)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.receptionists
    OWNER to postgres;

CREATE TABLE doctors (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    speciality VARCHAR(255),
	user_id bigint REFERENCES public.users (id)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.doctors
    OWNER to postgres;


CREATE TABLE patients (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
	birthdate DATE
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.patients
    OWNER to postgres;


CREATE TABLE appointments (
    id SERIAL PRIMARY KEY,
    doctor_id bigint REFERENCES public.doctors (id),
    patient_id bigint REFERENCES public.patients (id),
	appointment_date DATE,
    appointment_time TIME
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.appointments
    OWNER to postgres;


CREATE TABLE medicines (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
	symptom VARCHAR(255),
	intensity_degree VARCHAR(255)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.medicines
    OWNER to postgres;

CREATE TABLE medicines_stock (
    id SERIAL PRIMARY KEY,
    pharmacy_name VARCHAR(255),
	medicine VARCHAR(255),
	stock integer,
	UNIQUE (pharmacy_name, medicine)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.medicines_stock
    OWNER to postgres;


CREATE TABLE prescriptions (
    id SERIAL PRIMARY KEY,
    doctor_id bigint REFERENCES public.doctors (id),
    patient_id bigint REFERENCES public.patients (id),
	prescription_date date,
	prescription_time time
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.prescriptions
    OWNER to postgres;


CREATE TABLE prescription_medicines (
    prescription_id BIGINT REFERENCES public.prescriptions (id),
    medicine_id BIGINT REFERENCES public.medicines (id),
	dosage_per_day double precision,
	treatment_time integer,
    PRIMARY KEY (prescription_id, medicine_id)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.prescription_medicines
    OWNER to postgres;

CREATE TABLE symptoms_medical (
	symptom VARCHAR(255),
	medical_section VARCHAR(255),
	PRIMARY KEY (symptom, medical_section)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.symptoms_medical
    OWNER to postgres;

CREATE TABLE symptoms_patient (
    appointment_id bigint REFERENCES public.appointments (id),
    patient_id bigint REFERENCES public.patients (id),
	symptom VARCHAR(255),
	PRIMARY KEY (appointment_id, patient_id, symptom)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.symptoms_patient
    OWNER to postgres;


INSERT INTO symptoms_medical (symptom, medical_section)
VALUES
	('Febra', 'Medicină Generală'),
    ('Febra', 'Medicină Internă'),
    ('Febra', 'Chirurgie Generală'),
    ('Febra', 'Cardiologie'),
    ('Febra', 'Neurologie'),
    ('Febra', 'Oftalmologie'),
    ('Oboseală', 'Medicină Internă'),
    ('Oboseală', 'Neurologie'),
    ('Durere', 'Medicină Generală'),
    ('Durere', 'Pediatrie'),
    ('Durere', 'Chirurgie Generală'),
    ('Durere', 'Medicină Internă'),
    ('Durere', 'Neurologie'),
    ('Durere', 'Oftalmologie'),
    ('Dureri abdominale', 'Medicină Generală'),
    ('Dureri abdominale', 'Chirurgie Generală'),
    ('Dureri abdominale', 'Medicină Internă'),
    ('Dureri abdominale', 'Obstetrică și Ginecologie'),
    ('Tuse', 'Medicină Generală'),
    ('Tuse', 'Medicină Internă'),
    ('Grețuri și vărsături', 'Medicină Internă'),
    ('Grețuri și vărsături', 'Obstetrică și Ginecologie'),
    ('Pierdere în greutate', 'Medicină Generală'),
    ('Pierdere în greutate', 'Medicină Internă'),
    ('Dificultăți respiratorii', 'Medicină Generală'),
    ('Dificultăți respiratorii', 'Medicină Internă'),
    ('Dificultăți respiratorii', 'Cardiologie'),
    ('Umflături și inflamații', 'Chirurgie Generală'),
    ('Sângerări', 'Medicină Generală'),
    ('Sângerări', 'Chirurgie Generală'),
    ('Sângerări', 'Obstetrică și Ginecologie'),
    ('Slăbiciune sau amorțeală în membre', 'Neurologie'),
    ('Slăbiciune sau amorțeală în membre', 'Ortopedie'),
    ('Probleme de vedere', 'Oftalmologie'),
    ('Schimbări în tranzitul intestinal', 'Medicină Internă'),
    ('Schimbări în tranzitul intestinal', 'Gastroenterologie'),
    ('Palpitații', 'Medicină Generală'),
    ('Palpitații', 'Cardiologie');

--select create_doctor('marian','popescu','cardiolog')
-- select * from symptoms_medical;

INSERT INTO medicines (name, symptom, intensity_degree) VALUES
    ('Nurofen', 'Febra', 'Ușor'),
    ('Paracetamol', 'Febra', 'Moderat'),
    ('Ibuprofen', 'Febra', 'Sever'),
    ('Vitamina B12', 'Oboseală', 'Ușor'),
    ('Cafeină', 'Oboseală', 'Moderat'),
    ('Modafinil', 'Oboseală', 'Sever'),
    ('Aspirină', 'Durere', 'Ușor'),
    ('Ibuprofen', 'Durere', 'Moderat'),
    ('Morfina', 'Durere', 'Sever'),
    ('Sirop pentru tuse', 'Tuse', 'Ușor'),
    ('Elixir pentru tuse', 'Tuse', 'Moderat'),
    ('Antitusiv puternic', 'Tuse', 'Sever'),
    ('Dramamine', 'Grețuri și vărsături', 'Ușor'),
    ('Motilium', 'Grețuri și vărsături', 'Moderat'),
    ('Zofran', 'Grețuri și vărsături', 'Sever'),
    ('Supliment alimentar pentru îngrășare', 'Pierdere în greutate', 'Ușor'),
    ('Supliment caloric', 'Pierdere în greutate', 'Moderat'),
    ('Tratament specializat pentru îngrășare', 'Pierdere în greutate', 'Sever'),
    ('Ventolin', 'Dificultăți respiratorii', 'Ușor'),
    ('Seretide', 'Dificultăți respiratorii', 'Moderat'),
    ('Corticosteroizi inhalatori', 'Dificultăți respiratorii', 'Sever'),
    ('Ibuprofen', 'Umflături și inflamații', 'Ușor'),
    ('Diclofenac', 'Umflături și inflamații', 'Moderat'),
    ('Prednison', 'Umflături și inflamații', 'Sever'),
    ('Tranexamic acid', 'Sângerări', 'Ușor'),
    ('Acid aminocaproic', 'Sângerări', 'Moderat'),
    ('Factori de coagulare', 'Sângerări', 'Sever'),
    ('Vitamina B12', 'Slăbiciune sau amorțeală în membre', 'Ușor'),
    ('Magnesium', 'Slăbiciune sau amorțeală în membre', 'Moderat'),
    ('Medicamente pentru circulație', 'Slăbiciune sau amorțeală în membre', 'Sever'),
    ('Picături pentru ochi', 'Probleme de vedere', 'Ușor'),
    ('Lentile de contact', 'Probleme de vedere', 'Moderat'),
    ('Intervenție chirurgicală oftalmologică', 'Probleme de vedere', 'Sever');

-- Inserare combinații de farmacii și medicamente în tabela medicines_stock
INSERT INTO medicines_stock (pharmacy_name, medicine, stock)
VALUES
  -- Catena
  ('Catena', 'Nurofen', 1),
  ('Catena', 'Paracetamol', 100),
  ('Catena', 'Ibuprofen', 100),
  ('Catena', 'Vitamina B12', 100),
  ('Catena', 'Cafeină', 100),
  ('Catena', 'Modafinil', 100),
  ('Catena', 'Aspirină', 100),
  ('Catena', 'Morfina', 100),
  ('Catena', 'Sirop pentru tuse', 100),
  ('Catena', 'Elixir pentru tuse', 100),
  ('Catena', 'Antitusiv puternic', 100),
  ('Catena', 'Dramamine', 100),
  ('Catena', 'Motilium', 100),
  ('Catena', 'Zofran', 100),
  ('Catena', 'Supliment alimentar pentru îngrășare', 100),
  ('Catena', 'Supliment caloric', 100),
  ('Catena', 'Tratament specializat pentru îngrășare', 100),
  ('Catena', 'Ventolin', 100),
  ('Catena', 'Seretide', 100),
  ('Catena', 'Corticosteroizi inhalatori', 100),
  ('Catena', 'Ibuprofen', 100),
  ('Catena', 'Diclofenac', 100),
  ('Catena', 'Prednison', 100),
  ('Catena', 'Tranexamic acid', 100),
  ('Catena', 'Acid aminocaproic', 100),
  ('Catena', 'Factori de coagulare', 100),
  ('Catena', 'Vitamina B12', 100),
  ('Catena', 'Magnesium', 100),
  ('Catena', 'Medicamente pentru circulație', 100),
  ('Catena', 'Picături pentru ochi', 100),
  ('Catena', 'Lentile de contact', 100),
  ('Catena', 'Intervenție chirurgicală oftalmologică', 100),

  -- Ropharma
  ('Ropharma', 'Nurofen', 100),
  ('Ropharma', 'Paracetamol', 100),
  ('Ropharma', 'Ibuprofen', 100),
  ('Ropharma', 'Vitamina B12', 100),
  ('Ropharma', 'Cafeină', 100),
  ('Ropharma', 'Modafinil', 100),
  ('Ropharma', 'Aspirină', 100),
  ('Ropharma', 'Morfina', 100),
  ('Ropharma', 'Sirop pentru tuse', 100),
  ('Ropharma', 'Elixir pentru tuse', 100),
  ('Ropharma', 'Antitusiv puternic', 100),
  ('Ropharma', 'Dramamine', 100),
  ('Ropharma', 'Motilium', 100),
  ('Ropharma', 'Zofran', 100),
  ('Ropharma', 'Supliment alimentar pentru îngrășare', 100),
  ('Ropharma', 'Supliment caloric', 100),
  ('Ropharma', 'Tratament specializat pentru îngrășare', 100),
  ('Ropharma', 'Ventolin', 100),
  ('Ropharma', 'Seretide', 100),
  ('Ropharma', 'Corticosteroizi inhalatori', 100),
  ('Ropharma', 'Ibuprofen', 100),
  ('Ropharma', 'Diclofenac', 100),
  ('Ropharma', 'Prednison', 100),
  ('Ropharma', 'Tranexamic acid', 100),
  ('Ropharma', 'Acid aminocaproic', 100),
  ('Ropharma', 'Factori de coagulare', 100),
  ('Ropharma', 'Vitamina B12', 100),
  ('Ropharma', 'Magnesium', 100),
  ('Ropharma', 'Medicamente pentru circulație', 100),
  ('Ropharma', 'Picături pentru ochi', 100),
  ('Ropharma', 'Lentile de contact', 100),
  ('Ropharma', 'Intervenție chirurgicală oftalmologică', 100),

  -- Dr.Max
  ('Dr.Max', 'Nurofen', 100),
  ('Dr.Max', 'Paracetamol', 100),
  ('Dr.Max', 'Ibuprofen', 100),
  ('Dr.Max', 'Vitamina B12', 100),
  ('Dr.Max', 'Cafeină', 100),
  ('Dr.Max', 'Modafinil', 100),
  ('Dr.Max', 'Aspirină', 100),
  ('Dr.Max', 'Morfina', 100),
  ('Dr.Max', 'Sirop pentru tuse', 100),
  ('Dr.Max', 'Elixir pentru tuse', 100),
  ('Dr.Max', 'Antitusiv puternic', 100),
  ('Dr.Max', 'Dramamine', 100),
  ('Dr.Max', 'Motilium', 100),
  ('Dr.Max', 'Zofran', 100),
  ('Dr.Max', 'Supliment alimentar pentru îngrășare', 100),
  ('Dr.Max', 'Supliment caloric', 100),
  ('Dr.Max', 'Tratament specializat pentru îngrășare', 100),
  ('Dr.Max', 'Ventolin', 100),
  ('Dr.Max', 'Seretide', 100),
  ('Dr.Max', 'Corticosteroizi inhalatori', 100),
  ('Dr.Max', 'Ibuprofen', 100),
  ('Dr.Max', 'Diclofenac', 100),
  ('Dr.Max', 'Prednison', 100),
  ('Dr.Max', 'Tranexamic acid', 100),
  ('Dr.Max', 'Acid aminocaproic', 100),
  ('Dr.Max', 'Factori de coagulare', 100),
  ('Dr.Max', 'Vitamina B12', 100),
  ('Dr.Max', 'Magnesium', 100),
  ('Dr.Max', 'Medicamente pentru circulație', 100),
  ('Dr.Max', 'Picături pentru ochi', 100),
  ('Dr.Max', 'Lentile de contact', 100),
  ('Dr.Max', 'Intervenție chirurgicală oftalmologică', 100)
ON CONFLICT DO NOTHING;

CREATE OR REPLACE FUNCTION create_symptoms_patient(appointment_id bigint, patient_id bigint, symptom character varying)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('INSERT INTO symptoms_patient (appointment_id, patient_id, symptom) VALUES(%s,%s,%L)', appointment_id, patient_id, symptom);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_symptoms_patient(bigint, bigint, character varying)
OWNER TO postgres;



CREATE OR REPLACE FUNCTION create_user(p_email character varying, password_hash character varying, password_salt character varying, role character varying)
 RETURNS void AS
  $BODY$
    DECLARE
	  v_count integer;
    BEGIN
	SELECT COUNT(*) INTO v_count
	FROM users
	WHERE email = p_email;

		IF v_count > 0 THEN
			RAISE EXCEPTION 'User already exists';
		END IF;

      EXECUTE format('INSERT INTO users (email, password_hash, password_salt, role) VALUES(%L,%L,%L,%L)', p_email, password_hash, password_salt, role);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_user(character varying,character varying,character varying, character varying)
OWNER TO postgres;

-- select create_user('email@gmail.com','ceva1')
-- select ((find_user_by_email('email@gmail.com')) ->> 'id')::bigint

CREATE OR REPLACE FUNCTION find_symptoms(appointment_id bigint, patient_id bigint)
 RETURNS SETOF json AS
  $BODY$
    BEGIN
     RETURN QUERY EXECUTE format('SELECT row_to_json(t) FROM (SELECT symptom FROM symptoms_patient WHERE appointment_id=%s AND patient_id=%s) t', appointment_id, patient_id);
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION find_symptoms(appointment_id bigint, patient_id bigint)
OWNER TO postgres;

CREATE OR REPLACE FUNCTION find_medicine_of_symptom(prescription_id bigint, symptom character varying)
  RETURNS VARCHAR(255) AS
$BODY$
  DECLARE
  	result VARCHAR(255);
  BEGIN
    EXECUTE format('SELECT m.name FROM prescription_medicines p JOIN
                                   medicines m ON p.medicine_id=m.id WHERE prescription_id=%s
                                   AND symptom=%L', prescription_id, symptom) INTO result;
	RETURN result;
  END
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_medicine_of_symptom(bigint, character varying)
OWNER TO postgres;

-- SELECT m.name FROM prescription_medicines p JOIN
--                                    medicines m ON p.medicine_id=m.id WHERE prescription_id=1
--                                    AND symptom='Febra';

CREATE OR REPLACE FUNCTION create_pharmacist(p_first_name character varying, p_last_name character varying, p_pharmacy character varying, user_id bigint)
 RETURNS void AS
  $BODY$
    DECLARE
	  v_count integer;
    BEGIN
	SELECT COUNT(*) INTO v_count
	FROM pharmacists
	WHERE first_name = p_first_name
		AND last_name = p_last_name
		AND pharmacy = p_pharmacy;

		IF v_count > 0 THEN
			RAISE EXCEPTION 'Pharmacist already exists';
		END IF;

      EXECUTE format('INSERT INTO pharmacists (first_name, last_name, pharmacy, user_id) VALUES(%L,%L,%L,%s)', p_first_name, p_last_name, p_pharmacy, user_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_pharmacist(character varying,character varying,character varying,bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION create_receptionist(p_first_name character varying, p_last_name character varying, p_desk_id bigint, user_id bigint)
 RETURNS void AS
  $BODY$
    DECLARE
	  v_count integer;
    BEGIN
	SELECT COUNT(*) INTO v_count
	FROM receptionists
	WHERE first_name = p_first_name
		AND last_name = p_last_name
		AND desk_id = p_desk_id;

		IF v_count > 0 THEN
			RAISE EXCEPTION 'Receptionist already exists';
		END IF;
      EXECUTE format('INSERT INTO receptionists (first_name, last_name, desk_id, user_id) VALUES(%L,%L,%s,%s)', p_first_name, p_last_name, p_desk_id, user_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_receptionist(character varying, character varying, bigint, bigint)
OWNER TO postgres;

CREATE OR REPLACE FUNCTION create_doctor(p_first_name character varying, p_last_name character varying, p_speciality character varying, p_user_id bigint)
RETURNS void AS
$BODY$
DECLARE
	v_count integer;
BEGIN
	SELECT COUNT(*) INTO v_count
	FROM doctors
	WHERE first_name = p_first_name
		AND last_name = p_last_name
		AND speciality = p_speciality;

		IF v_count > 0 THEN
			RAISE EXCEPTION 'Doctor already exists';
		END IF;
      EXECUTE format('INSERT INTO doctors (first_name, last_name, speciality, user_id) VALUES (%L, %L, %L, %s)',
				   p_first_name, p_last_name, p_speciality, p_user_id);
END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_doctor(character varying, character varying, character varying, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION create_appointment(doctor_id bigint, patient_id bigint, appointment_date character varying, appointment_time character varying)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('INSERT INTO appointments (doctor_id , patient_id , appointment_date , appointment_time) VALUES(%s,%s,%L,%L)', doctor_id , patient_id , appointment_date , appointment_time);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_appointment(bigint, bigint, character varying, character varying)
OWNER TO postgres;

CREATE OR REPLACE FUNCTION create_medicines_brand(name character varying)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('INSERT INTO medicines_brands (name) VALUES(%L)', name);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_medicines_brand(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION create_medicine(name character varying, stock integer, brand_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('INSERT INTO medicines (name, stock, brand_id) VALUES(%L,%s,%s)', name, stock, brand_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_medicine(character varying, integer, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION create_patient(first_name character varying, last_name character varying, birthdate character varying)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('INSERT INTO patients (first_name, last_name, birthdate) VALUES(%L,%L,%L)', first_name, last_name, birthdate);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_patient(character varying, character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION create_prescription(doctor_id bigint, patient_id bigint, prescription_date character varying, prescription_time character varying)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('INSERT INTO prescriptions (doctor_id, patient_id, prescription_date, prescription_time) VALUES(%s,%s,%L,%L)', doctor_id, patient_id, prescription_date, prescription_time);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_prescription(bigint, bigint, character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_appointments_by_doctor(v_doctor_id bigint)
  RETURNS SETOF json AS
$BODY$
DECLARE
  query_text text;
  result json;
  v_counter integer;
BEGIN
  v_counter := 0;
  SELECT COUNT(*) INTO v_counter FROM appointments WHERE doctor_id=v_doctor_id;

  IF v_counter = 0 THEN
  	RAISE EXCEPTION 'No data found';
  END IF;

  RETURN QUERY EXECUTE format('SELECT row_to_json(t) FROM (SELECT * FROM appointments WHERE doctor_id=%s) t', v_doctor_id);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_appointments_by_doctor(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_doctor_by_user_id(v_user_id bigint)
  RETURNS json AS
$BODY$
DECLARE
  query_text text;
  result json;
BEGIN
  query_text := format('SELECT row_to_json(row)
  FROM (SELECT * FROM doctors WHERE user_id = %s LIMIT 1) row', v_user_id);

  FOR result IN EXECUTE query_text LOOP
    RETURN result;
  END LOOP;

  IF result IS NULL THEN
  	RAISE EXCEPTION 'User id is not a doctor';
  END IF;

  RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_doctor_by_user_id(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_receptionist_by_user_id(v_user_id bigint)
  RETURNS json AS
$BODY$
DECLARE
  query_text text;
  result json;
BEGIN
  query_text := format('SELECT row_to_json(row)
  FROM (SELECT * FROM receptionists WHERE user_id = %s LIMIT 1) row', v_user_id);

  FOR result IN EXECUTE query_text LOOP
    RETURN result;
  END LOOP;

  IF result IS NULL THEN
  	RAISE EXCEPTION 'User id is not a receptionist';
  END IF;

  RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_receptionist_by_user_id(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_pharmacist_by_user_id(v_user_id bigint)
  RETURNS json AS
$BODY$
DECLARE
  query_text text;
  result json;
BEGIN
  query_text := format('SELECT row_to_json(row)
  FROM (SELECT * FROM pharmacists WHERE user_id = %s LIMIT 1) row', v_user_id);

  FOR result IN EXECUTE query_text LOOP
    RETURN result;
  END LOOP;

  IF result IS NULL THEN
  	RAISE EXCEPTION 'User id is not a pharmacist';
  END IF;

  RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_pharmacist_by_user_id(bigint)
OWNER TO postgres;



CREATE OR REPLACE FUNCTION find_by_id(v_table_name character varying, v_id bigint)
  RETURNS json AS
$BODY$
DECLARE
  query_text text;
  result json;
  v_count integer;
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM public.%I WHERE id=%s',v_table_name, v_id) INTO v_count;
  IF v_count = 0 THEN
  	RAISE EXCEPTION 'Invalid id!';
  END IF;
  query_text := format('SELECT row_to_json(row)
  FROM (SELECT * FROM public.%I WHERE id = %s LIMIT 1) row', v_table_name, v_id);

  FOR result IN EXECUTE query_text LOOP
    RETURN result;
  END LOOP;

  RETURN NULL;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_by_id(character varying, bigint)
OWNER TO postgres;
-- select * from find_by_id('appointments',1)

CREATE OR REPLACE FUNCTION find_by_full_name(v_table_name character varying, first_name character varying, last_name character varying)
  RETURNS SETOF json AS
$BODY$
BEGIN
    RETURN QUERY EXECUTE format('SELECT row_to_json(t)
								FROM (SELECT * FROM %I
								WHERE first_name=%L AND last_name=%L) t', v_table_name, first_name, last_name);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_by_full_name(character varying, character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_patient_by_everything(first_name character varying, last_name character varying, birthdate character varying)
  RETURNS json AS
$BODY$
DECLARE
	result json;
BEGIN
    EXECUTE format('SELECT row_to_json(row) FROM (SELECT * FROM patients WHERE first_name=%L
				   AND last_name=%L AND birthdate=%L LIMIT 1) row',first_name,last_name,birthdate)
				   INTO result;
	RETURN result;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_patient_by_everything(character varying, character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_prescription_by_everything(doctor_id bigint, patient_id bigint, prescription_date character varying, prescription_time character varying)
  RETURNS json AS
$BODY$
DECLARE
	result json;
BEGIN
    EXECUTE format('SELECT row_to_json(row) FROM (SELECT * FROM prescriptions WHERE doctor_id=%s
				   AND patient_id=%s AND prescription_date=%L AND prescription_time=%L LIMIT 1) row',doctor_id,patient_id,prescription_date,prescription_time)
				   INTO result;
	RETURN result;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_prescription_by_everything(bigint, bigint, character varying, character varying)
OWNER TO postgres;



CREATE OR REPLACE FUNCTION find_appointment_by_everything(doctor_id bigint, appointment_date character varying, appointment_time character varying)
  RETURNS json AS
$BODY$
DECLARE
	result json;
BEGIN
    EXECUTE format('SELECT row_to_json(row) FROM (SELECT * FROM appointments WHERE doctor_id=%s
				   AND appointment_date=%L AND appointment_time=%L LIMIT 1) row',doctor_id,appointment_date,appointment_time)
				   INTO result;
	RETURN result;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_appointment_by_everything(bigint, character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_doctor_by_speciality(speciality character varying)
  RETURNS SETOF json AS
$BODY$
BEGIN
    RETURN QUERY EXECUTE format('SELECT row_to_json(t)
								FROM (SELECT * FROM doctors WHERE speciality=%L) t', speciality);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_doctor_by_speciality(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION delete_by_id(v_table_name character varying, v_id bigint)
  RETURNS void AS
$BODY$
BEGIN
  EXECUTE format('DELETE FROM public.%I WHERE id = %s', v_table_name, v_id);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION delete_by_id(character varying, bigint)
OWNER TO postgres;

CREATE OR REPLACE FUNCTION delete_symptoms( v_id bigint)
  RETURNS void AS
$BODY$
BEGIN
  EXECUTE format('DELETE FROM symptoms_patient WHERE appointment_id = %s', v_id);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION delete_symptoms(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION delete_all(v_table_name character varying)
  RETURNS void AS
$BODY$
BEGIN
  EXECUTE format('DELETE FROM public.%I', v_table_name);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION delete_all(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION remove_medicines(prescription_id bigint)
  RETURNS void AS
$BODY$
BEGIN
  EXECUTE format('DELETE FROM prescription_medicines WHERE prescription_id=%s', prescription_id);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION remove_medicines(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION remove_medicine(prescription_id bigint, medicine character varying)
  RETURNS void AS
$BODY$
DECLARE
	med_id bigint;
BEGIN
  EXECUTE format('SELECT m.id from medicines m JOIN prescription_medicines p ON
				 m.id=p.medicine_id WHERE m.name=%L', medicine) INTO med_id;
  EXECUTE format('DELETE FROM prescription_medicines WHERE prescription_id=%s AND medicine_id=%L', prescription_id, med_id);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION remove_medicine(bigint, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_all(v_table_name character varying)
RETURNS SETOF json AS
$BODY$
DECLARE
	v_count integer;
BEGIN
	v_count := 0;
	EXECUTE format('SELECT COUNT(*) FROM %I', v_table_name) INTO v_count;
	IF v_count = 0 THEN
		RAISE EXCEPTION 'No data found';
	END IF;
    RETURN QUERY EXECUTE format('SELECT row_to_json(t) FROM (SELECT * FROM %I) t', v_table_name);
END;
$BODY$
LANGUAGE plpgsql  VOLATILE
COST 100;
ALTER FUNCTION find_all(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_receptionist_by_desk_id(desk_id bigint)
  RETURNS SETOF json AS
$BODY$
BEGIN
    RETURN QUERY EXECUTE format('SELECT row_to_json(t)
								FROM (SELECT * FROM receptionists WHERE desk_id=%s) t', desk_id);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_receptionist_by_desk_id(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_pharmacist_by_pharmacy(pharmacy character varying)
  RETURNS SETOF json AS
$BODY$
BEGIN
    RETURN QUERY EXECUTE format('SELECT row_to_json(t)
								FROM (SELECT * FROM pharmacists WHERE pharmacy=%L) t', pharmacy);
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_pharmacist_by_pharmacy(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_user_by_email(email character varying)
  RETURNS json AS
$BODY$
DECLARE
  query_text text;
  result json;
BEGIN
  query_text := format('SELECT row_to_json(t)
								FROM (SELECT * FROM users WHERE email=%L  LIMIT 1) t', email);

   EXECUTE query_text INTO result;

  RETURN result;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION find_user_by_email(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION update_prescription(doctor_id bigint, patient_id bigint, prescription_date character varying, prescription_time character varying, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE prescriptions SET doctor_id=%s, partient_id=%s, prescription_date=%L, prescription_time=%L WHERE id=%s', doctor_id, patient_id, prescription_date, prescription_time, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_prescription(bigint, bigint, character varying, character varying, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION update_patient(first_name character varying, last_name character varying, birthdate character varying, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE patients SET first_name=%L, last_name=%L, birthdate=%L WHERE id=%s', first_name, last_name, birthdate, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_patient(character varying, character varying, character varying, bigint)
OWNER TO postgres;



CREATE OR REPLACE FUNCTION update_appointment(doctor_id bigint, patient_id bigint, appointment_date character varying, appointment_time character varying, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE appointments SET doctor_id=%s , patient_id=%s , appointment_date=%L , appointment_time=%L WHERE id=%s', doctor_id , patient_id , appointment_date , appointment_time, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_appointment(bigint, bigint, character varying, character varying, bigint)
OWNER TO postgres;

CREATE OR REPLACE FUNCTION update_doctor(first_name character varying, last_name character varying, speciality character varying, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE doctors SET first_name=%L, last_name=%L, speciality=%L WHERE id=%s', first_name, last_name, speciality, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_doctor(character varying,character varying,character varying, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION update_pharmacist(first_name character varying, last_name character varying, pharmacy character varying, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE pharmacists SET first_name=%L, last_name=%L, pharmacy=%L WHERE id=%s', first_name, last_name, pharmacy, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_pharmacist(character varying,character varying,character varying, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION update_receptionist(first_name character varying, last_name character varying, desk_id bigint, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE receptionists SET first_name=%L, last_name=%L, desk_id=%s WHERE id=%s', first_name, last_name, desk_id, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_receptionist(character varying,character varying, bigint, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION update_user(email character varying, password character varying, v_id bigint)
 RETURNS void AS
  $BODY$
    BEGIN
      EXECUTE format('UPDATE users SET email=%L, password=%L WHERE id=%s', email, password, v_id);
    END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_user(character varying,character varying, bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION update_stock(medicine character varying, pharmacy character varying, dosage double precision, duration integer)
 RETURNS void AS
  $BODY$
    BEGIN
     EXECUTE format('UPDATE medicines_stock SET stock=stock-%s*%s WHERE medicine=%L AND pharmacy_name=%L',dosage, duration, medicine, pharmacy) ;
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_stock(character varying, character varying, double precision, integer)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION refill_medicine(pharmacy character varying, medicine character varying)
 RETURNS void AS
  $BODY$
    BEGIN
     EXECUTE format('UPDATE medicines_stock SET stock=100 WHERE medicine=%L AND pharmacy_name=%L', medicine, pharmacy);
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION refill_medicine(character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION create_prescription_medicine(v_prescription_id bigint, v_symptom character varying, v_intensity character varying)
RETURNS VOID AS
$BODY$
BEGIN
    INSERT INTO prescription_medicines (prescription_id, medicine_id)
    VALUES (v_prescription_id,(SELECT id
    FROM medicines
    WHERE symptom = v_symptom AND intensity_degree = v_intensity));
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION create_prescription_medicine(bigint, character varying, character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_medical_section_with_max_symptoms(symptoms_list text[], birth_date date)
  RETURNS text AS
$$
DECLARE
  max_count integer := 0;
  max_section text := '';
  v_count integer;
  section text;
  age integer;
BEGIN
  age := DATE_PART('year', AGE(birth_date));

  IF age < 12 THEN
    RETURN 'Pediatrie';
  END IF;

  FOR section IN SELECT DISTINCT medical_section FROM symptoms_medical LOOP
    SELECT COUNT(*) INTO v_count
    FROM symptoms_medical
    WHERE symptom = ANY(symptoms_list)
    AND medical_section = section;

    IF v_count > max_count THEN
      max_count := v_count;
      max_section := section;
    ELSIF v_count = max_count THEN
      max_section := 'Medicină Generală';
    END IF;
  END LOOP;

  RETURN max_section;
END;
$$
LANGUAGE plpgsql;

-- SELECT find_medical_section_with_max_symptoms(ARRAY['Febra', 'Oboseală', 'Durere', 'Tuse', 'Grețuri și vărsături'],'2002-08-05')

CREATE OR REPLACE FUNCTION find_available_appointments(symptoms_list text[], birth_date date)
  RETURNS SETOF json AS
$$
DECLARE
  medical_section text;
  available_doctors CURSOR FOR
    SELECT id
    FROM doctors
    WHERE speciality = medical_section
    AND id NOT IN (SELECT doctor_id FROM appointments)
    ORDER BY id;
  scheduled_doctors CURSOR FOR
    SELECT a.doctor_id,
           MAX(a.appointment_date) AS max_appointment_date,
           MAX(a.appointment_time) AS max_appointment_time
    FROM appointments a
    INNER JOIN doctors d ON a.doctor_id = d.id
    WHERE d.speciality = medical_section
    GROUP BY a.doctor_id, d.first_name, d.last_name
    ORDER BY a.doctor_id;
  doctor_id bigint;
  appointment_date date;
  appointment_time time;
  max_appointment_date date;
  max_appointment_time time;
  current_date date := CURRENT_DATE;
  current_time time := CURRENT_TIME;
BEGIN
  medical_section := find_medical_section_with_max_symptoms(symptoms_list, birth_date);

  OPEN available_doctors;

  -- Selectarea doctorilor disponibili care nu au programări
  FETCH NEXT FROM available_doctors INTO doctor_id;
  WHILE FOUND LOOP
    appointment_date := current_date;
    appointment_time := '8:00';

    -- Adăugarea următoarei date și ore disponibile
    LOOP
      EXIT WHEN (appointment_date > current_date OR (appointment_date = current_date AND appointment_time >= current_time))
        AND EXTRACT(DOW FROM appointment_date) BETWEEN 1 AND 5;
      appointment_time := appointment_time + INTERVAL '1 hour';
      IF appointment_time > '16:00' THEN
        appointment_time := '8:00';
        appointment_date := appointment_date + INTERVAL '1 day';
      END IF;
    END LOOP;

    RETURN NEXT json_build_object(
      'doctor_id', doctor_id,
      'appointment_date', appointment_date,
      'appointment_time', appointment_time
    );

    FETCH NEXT FROM available_doctors INTO doctor_id;
  END LOOP;

  CLOSE available_doctors;

  OPEN scheduled_doctors;

  FETCH NEXT FROM scheduled_doctors INTO doctor_id, max_appointment_date, max_appointment_time;
  WHILE FOUND LOOP
    appointment_time := max_appointment_time + INTERVAL '1 hour';
	appointment_date := max_appointment_date;
    IF appointment_time > '16:00' THEN
      appointment_time := '8:00';
      appointment_date := max_appointment_date + INTERVAL '1 day';
    END IF;

    RETURN NEXT json_build_object(
      'doctor_id', doctor_id,
      'appointment_date', appointment_date,
      'appointment_time', appointment_time
    );

    FETCH NEXT FROM scheduled_doctors INTO doctor_id, max_appointment_date, max_appointment_time;
  END LOOP;

  RETURN;
END;
$$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION set_dosage_duration(prescription_id bigint, medicine character varying, dosage double precision, duration integer)
RETURNS void AS
$BODY$
BEGIN
	EXECUTE format('UPDATE prescription_medicines SET dosage_per_day=%s, treatment_time=%s
				   WHERE prescription_id=%L AND medicine_id IN (SELECT id from medicines WHERE name=%L)',
				   dosage,duration,prescription_id,medicine);
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION set_dosage_duration(bigint, character varying, double precision, integer)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_medicine_pharmacies(medicine character varying, dosage double precision, duration integer)
 RETURNS SETOF json AS
  $BODY$
  	DECLARE
	  v_count integer;
    BEGIN
	 EXECUTE format('SELECT COUNT(*) FROM medicines_stock WHERE medicine=%L AND %s*%s <= stock',
				   medicine,dosage,duration) INTO v_count;
	 IF v_count = 0 THEN
	 	RAISE EXCEPTION 'No pharmacies';
	 END IF;
     RETURN QUERY EXECUTE format('SELECT row_to_json(t) FROM (SELECT pharmacy_name FROM
								 medicines_stock WHERE medicine=%L AND %s*%s <= stock) t', medicine, dosage, duration);
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION find_medicine_pharmacies(character varying, double precision, integer)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_prescription_medicines(prescription_id bigint)
 RETURNS SETOF json AS
  $BODY$
  	DECLARE
	  v_count integer;
    BEGIN
	 EXECUTE format('SELECT COUNT(*) FROM prescriptions WHERE id=%s', prescription_id) INTO v_count;
	 IF v_count = 0 THEN
	 	RAISE EXCEPTION 'Prescription does not exist';
	 END IF;
     RETURN QUERY EXECUTE format('SELECT row_to_json(t) FROM (SELECT m.name, CAST(p.dosage_per_day AS VARCHAR),
								 CAST(p.treatment_time AS VARCHAR) FROM
								 medicines m JOIN prescription_medicines p ON m.id=p.medicine_id
								 WHERE prescription_id=%s) t', prescription_id);
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION find_prescription_medicines(bigint)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_pharmacy_medicines(pharmacy character varying)
 RETURNS SETOF json AS
  $BODY$
    BEGIN
     RETURN QUERY EXECUTE format('SELECT row_to_json(t) FROM (SELECT medicine, CAST(stock AS VARCHAR)
								FROM medicines_stock WHERE pharmacy_name=%L) t', pharmacy);
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION find_pharmacy_medicines(character varying)
OWNER TO postgres;


CREATE OR REPLACE FUNCTION find_pharmacy_stock(medicine character varying, pharmacy character varying)
 RETURNS integer AS
  $BODY$
    DECLARE
		result integer;
    BEGIN
     EXECUTE format('SELECT stock from medicines_stock WHERE medicine=%L
					AND pharmacy_name=%L',medicine, pharmacy) INTO result;
	 RETURN result;
	END
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION find_pharmacy_stock(character varying, character varying)
OWNER TO postgres;


-- select * from find_prescription_medicines(1);


COMMIT;