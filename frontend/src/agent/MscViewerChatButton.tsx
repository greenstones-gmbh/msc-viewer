import { AssistantButton } from "@greenstones/qui-ai";
import { useMscViewerChat } from "./use-msc-viewer-chat";

export function ApplicationAgentButton() {
  const chat = useMscViewerChat();
  return <AssistantButton chat={chat} />;
}
