const BASE_URL = `${import.meta.env.VITE_AUTH_URL}`;

export const authClient = {
    getToken: async(url: string, data: URLSearchParams) => {
        const response = await fetch(`${BASE_URL}/${url}`, {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-type": "application/x-www-form-urlencoded",
            },
            body: data.toString()
        });
        if (!response.ok) {
            throw new Error("Failed loading data");
        }
        return response.json();
    },
}
