import { ChatPage } from "@greenstones/qui-ai";
import {
  BaseFunctionCall,
  FunctionCallProps,
} from "@greenstones/qui-ai/src/chat/messages/FunctionCall";
import { InfoPopup } from "@greenstones/qui-bootstrap";
import { useAppChat } from "../agent/AppAssistant";

export function AppChatPage() {
  const chat = useAppChat();

  return (
    <ChatPage
      examples={[
        "find cell 10000",
        "show all lacs",
        "find gcas without gid 500",
      ]}
      chat={chat}
      defaultFunctionCall={QueryGraphFunctionCall}
    />
  );
}

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
