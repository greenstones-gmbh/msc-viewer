import { ChatPage } from "@greenstones/qui-ai";
import { useMscViewerChat } from "./use-msc-viewer-chat";

export function AppChatPage() {
  const chat = useMscViewerChat();
  return <ChatPage chat={chat} />;
}
