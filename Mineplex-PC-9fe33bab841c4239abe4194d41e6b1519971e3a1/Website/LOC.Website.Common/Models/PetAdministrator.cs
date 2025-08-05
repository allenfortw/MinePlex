namespace LOC.Website.Common.Models
{
    using System.Collections.Generic;
    using System.Data.Entity;
    using System.Linq;
    using Core.Model.Account;
    using Core.Model.Server;
    using Core.Tokens.Client;
    using Data;

    public class PetAdministrator : IPetAdministrator
    {
        private readonly INautilusRepositoryFactory _repositoryFactory;

        public PetAdministrator(INautilusRepositoryFactory repositoryFactory)
        {
            _repositoryFactory = repositoryFactory;
        }

        public List<Pet> GetPets(List<Pet> petTokens)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                foreach (var item in petTokens.Where(item => !repository.Any<Pet>(x => x.PetType == item.PetType)))
                {
                    repository.Add(item);
                }

                repository.CommitChanges();

                return repository.GetAll<Pet>().Include(x => x.SalesPackage).ToList();
            }
        }

        public List<PetExtra> GetPetExtras(List<PetExtra> petExtraTokens)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                foreach (var item in petExtraTokens.Where(item => !repository.Any<PetExtra>(x => x.Name == item.Name && x.Material == item.Material)))
                {
                    repository.Add(item);
                }

                repository.CommitChanges();

                return repository.GetAll<PetExtra>().Include(x => x.SalesPackage).ToList();
            }
        }

        public void AddPet(PetChangeToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == token.Name).Include(x => x.Pets).FirstOrDefault();

                if (account == null)
                    return;

                account.Pets.Add(new OwnedPet { PetType = token.PetType, PetName = token.PetName });

                repository.CommitChanges();
            }
        }

        public void UpdatePet(PetChangeToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == token.Name).Include(x => x.Pets).FirstOrDefault();

                if (account == null)
                    return;

                var pet = account.Pets.FirstOrDefault(x => x.PetType.Equals(token.PetType));

                if (pet == null)
                    return;

                pet.PetName = token.PetName;

                repository.Edit(pet);

                repository.CommitChanges();
            }
        }

        public void RemovePet(PetChangeToken token)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == token.Name).Include(x => x.Pets).FirstOrDefault();

                if (account == null)
                    return;

                var ownedPet = account.Pets.FirstOrDefault(x => x.PetType == token.PetType);

                if (ownedPet == null)
                    return;

                account.Pets.Remove(ownedPet);

                repository.CommitChanges();
            }
        }

        public void AddPetNameTag(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == name).FirstOrDefault();

                if (account == null)
                    return;

                account.PetNameTagCount++;

                repository.CommitChanges();
            }
        }

        public void RemovePetNameTag(string name)
        {
            using (var repository = _repositoryFactory.CreateRepository())
            {
                var account = repository.Where<Account>(x => x.Name == name).FirstOrDefault();

                if (account == null)
                    return;

                account.PetNameTagCount--;

                if (account.PetNameTagCount < 0)
                    account.PetNameTagCount = 0;

                repository.CommitChanges();
            }
        }
    }
}
