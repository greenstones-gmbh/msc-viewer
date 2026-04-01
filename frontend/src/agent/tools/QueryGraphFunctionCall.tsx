import {
  BaseFunctionCall,
  FunctionCallProps,
} from "@greenstones/qui-ai/src/chat/messages/FunctionCall";
import { InfoPopup } from "@greenstones/qui-bootstrap";

export function QueryGraphFunctionCall(props: FunctionCallProps) {
  return (
    <BaseFunctionCall
      {...props}
      renderCall={(args) => "Run Query: " + args.query}
      renderResultData={({ data }) => (
        <>
          {data.status} <InfoPopup v={data.data} />
        </>
      )}
    />
  );
}
