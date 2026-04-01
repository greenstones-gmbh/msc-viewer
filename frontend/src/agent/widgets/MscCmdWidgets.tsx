import { Widgets } from "@greenstones/qui-ai";
import { ConfirmButton } from "@greenstones/qui-bootstrap";

export const MscCmdWidgets: Widgets = {
  code: ({ inline, children, className, ...props }) => {
    if (className === "language-msc-commands") {
      return (
        <>
          <code>{children}</code>
          <div>
            <ConfirmButton
              style={{
                fontFamily: "var(--bs-body-font-family)",
                fontSize: "var(--bs-body-font-size)",
                fontWeight: "var(--bs-body-font-weight)",
                lineHeight: "var(--bs-body-line-height)",
                whiteSpace: "normal",
              }}
              label="Execute"
              onClick={async () => {}}
              className="mt-3 ms-0"
              confirmSize="lg"
              confirmBody={
                <>
                  The following commands will be executed in the MMS instance:
                  <pre
                    className="mt-3"
                    style={{ maxHeight: 300, overflow: "auto" }}
                  >
                    <code>{children}</code>
                  </pre>
                </>
              }
              confirmTitle={"Confirm execution"}
            />
          </div>
        </>
      );
    }

    return inline ? (
      <code className="bg-light text-danger rounded">{children}</code>
    ) : (
      <code>{children}</code>
    );
  },
};
