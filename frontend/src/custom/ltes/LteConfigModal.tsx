import { PropsWithChildren } from "react";
import { Button, Col, Form, Modal, Row } from "react-bootstrap";
import { FormProvider, useForm } from "react-hook-form";

import { InputField } from "@clickapp/qui-bootstrap";
import { getMscPropValue } from "../../msc/MsgObj";
import { CellPickerField } from "../cells/CellPickerModal";

export type LteConfigModalFields = {
  bts: any;
  eci: string;
  emcc: string;
  emnc: string;

  name: string;
  no: string;
  lac: string;

  ci: string;
  mcc: string;
  mnc: string;
};

export function LteConfigModal({
  handleClose,
  children,
  mscId,
}: PropsWithChildren<{ handleClose: any; mscId: string }>) {
  const methods = useForm<LteConfigModalFields>();

  const {
    handleSubmit,
    //getValues,
    watch,
    //formState: { errors, isSubmitted },
  } = methods;

  const values = watch();

  //const onSubmit: SubmitHandler<Inputs> = async (data) => {};
  const onSubmit = handleSubmit(async (data) => {
    console.log(data);
    handleClose();
  });

  return (
    <FormProvider {...methods}>
      <Modal
        show={true}
        onHide={handleClose}
        backdrop="static"
        keyboard={true}
        size="lg"
      >
        <Form
          noValidate
          onSubmit={onSubmit}
          // validated={isSubmitted}
        >
          <Modal.Header closeButton>
            <Modal.Title as="h5">Create LTE Configuration</Modal.Title>
          </Modal.Header>

          <Modal.Body>
            <CellPickerField
              name="bts"
              mscId={mscId}
              ops={{
                required: "BTS is required",
              }}
            />
            {/* <p className="fw-bold mt-4">BTS IDENTIFICATION</p>
            <Row>
              <Col>
                <InputField name="name" label="BTS NAME" />
              </Col>
              <Col>
                <InputField name="no" label="BTS NUMBER" />
              </Col>
              <Col>
                <InputField name="lac" label="LAC" />
              </Col>
            </Row>
            <Row>
              <Col>
                <InputField name="ci" label="CI" />
              </Col>
              <Col>
                <InputField name="mcc" label="MCC" />
              </Col>
              <Col>
                <InputField name="mnc" label="MNC" />
              </Col>
            </Row> */}
            <p className="fw-bold mt-5">E-UTRAN CELL GLOBAL IDENTITY</p>
            <Row>
              <Col>
                <InputField
                  name="eci"
                  label="ECI"
                  ops={{
                    required: "ECI is required",
                    pattern: {
                      value: /\d+/g,
                      message: "Entered value does not match format",
                    },
                  }}
                />
              </Col>
              <Col>
                <InputField
                  name="emcc"
                  label="EMCC"
                  ops={{
                    required: "EMCC is required",
                    value: "998",
                    pattern: {
                      value: /\d\d\d$/g,
                      message: "Entered value does not match format",
                    },
                  }}
                />
              </Col>
              <Col>
                <InputField
                  name="emnc"
                  label="EMNC"
                  ops={{
                    required: "EMNC is required",
                    value: "01",
                    pattern: {
                      value: /\d\d$/g,
                      message: "Entered value does not match format",
                    },
                  }}
                />
              </Col>
            </Row>
            {children}
            {/* Command deleteLteConfig = new Command("ZEPG:ECGI:ECI,EMCC,EMNC;",
            null);  */}

            <p className="fw-bold mt-5 mb-1">Command</p>
            <div className="bg-light p-2  rounded border font-monospace">
              ZEPE:ECGI:ECI=<b>{values.eci || ""}</b>,EMCC=
              <b>{values.emcc || ""}</b>
              ,EMNC=
              <b>{values.emnc || ""}</b>:TYPE=BTS,NO=
              <b>
                {values.bts ? getMscPropValue(values.bts, "BTS", "NUMBER") : ""}
              </b>
              ;
            </div>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
              Cancel
            </Button>
            <Button
              variant="primary"
              type="submit"

              // disabled={isSubmitting || !isValid}
              // disabled={
              //   isSubmitting || !successButtonEnabled
              //   //||(successButtonEnabledOnlyOnValid && !isValid)
              // }
            >
              {/* {isSubmitting && <Spinner animation="border" size="sm" />}{" "}
            {successButtonLabel} */}
              Create / Preview
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </FormProvider>
  );
}
