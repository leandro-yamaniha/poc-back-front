FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy csproj and restore as distinct layers
COPY ["BeautySalonAPI/BeautySalonAPI.csproj", "BeautySalonAPI/"]
COPY ["BeautySalonAPI.Tests/BeautySalonAPI.Tests.csproj", "BeautySalonAPI.Tests/"]
RUN dotnet restore "BeautySalonAPI/BeautySalonAPI.csproj"

# Copy everything else and build
COPY . .
WORKDIR "/src/BeautySalonAPI"
RUN dotnet build "BeautySalonAPI.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "BeautySalonAPI.csproj" -c Release -o /app/publish

# Build runtime image
FROM mcr.microsoft.com/dotnet/aspnet:8.0
WORKDIR /app
COPY --from=publish /app/publish .
EXPOSE 8080
ENTRYPOINT ["dotnet", "BeautySalonAPI.dll"]
